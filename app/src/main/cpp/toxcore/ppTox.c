//
// Created by 胡智鹏 on 2019/1/7.
// https://www.jianshu.com/p/8661e0b16d1c
// https://wiki.tox.chat/developers/client_examples/echo_bot
// https://zetok.github.io/tox-spec/
//
#include "stdio.h"
#include "logger.h"
#include "tox.h"
#include "syslog.h"
#include "ccompat.h"
#include "ppTox.h"
#include "Messenger.h"
#include <jni.h>
#include <android/log.h>
#include <malloc.h>
#include <libsodium/include/sodium.h>
#include <ctype.h>
#include <unistd.h>
#include <time.h>

typedef struct {
    uint8_t id[TOX_PUBLIC_KEY_SIZE];
    uint8_t accepted;
} Friend_request;

static Friend_request pending_requests[256];
static uint8_t num_requests = 0;

#define NUM_FILE_SENDERS 100
typedef struct {
    uint64_t filesize;
    FILE *file;
    uint32_t friendnum;
    uint32_t filenumber;
} File_Sender;
static File_Sender file_senders[NUM_FILE_SENDERS];
static uint8_t numfilesenders;
static uint64_t received_file_size;

Tox *mTox = NULL;
JNIEnv *Env;
JavaVM *g_jvm = NULL;
jobject g_obj = NULL;


#define LOG_TAG  "C_TAG"
#define LOGD(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define FRADDR_TOSTR_CHUNK_LEN 8

static const char *data_file_name = NULL;
char dataPathFile[200] = {0};
const char *savedata_filename = "toxId.json";

#define FRAPUKKEY_TOSTR_BUFSIZE (TOX_PUBLIC_KEY_SIZE * 2 + 1)

#define FRADDR_TOSTR_BUFSIZE (TOX_ADDRESS_SIZE * 2 + TOX_ADDRESS_SIZE / FRADDR_TOSTR_CHUNK_LEN + 1)
tox_self_connection_status_cb self_connection_status_cb;
tox_friend_request_cb friend_request_cb;
tox_friend_message_cb friend_message_cb;
tox_friend_connection_status_cb friend_connection_status_cb;
//
tox_file_chunk_request_cb file_chunk_request_cb;
// 接收到文件的回调
tox_file_recv_cb file_recv_cb;
// 接收到文件块的回调
tox_file_recv_chunk_cb file_recv_chunk_cb;
//
tox_file_recv_control_cb file_recv_control_cb;

char recv_filename[200] = {0};
int recv_filesize = 0;

JNIEXPORT void JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_createTox(JNIEnv *env, jobject thiz, jstring dataPath) {
    Env = env;
    (*Env)->GetJavaVM(Env, &g_jvm);
    g_obj = (*Env)->NewGlobalRef(Env, thiz);

    char *dataPath_p = Jstring2CStr(Env, dataPath);
    // strcat 将两个char连接起来
    data_file_name = strcat(dataPath_p, savedata_filename);
    //strcpy 字符串复制
    strcpy(dataPathFile, dataPath_p);
    mTox = load_data();
//    const char *name = "ppm Tox";
//    tox_self_set_name(mTox, name, strlen(name), NULL);
//    const char *status_message = "ppm your messages";
//    tox_self_set_status_message(mTox, status_message, strlen(status_message), NULL);
    save_data_file(mTox, data_file_name);
    tox_callback_friend_connection_status(mTox, friend_connection_status_cb, NULL);
    tox_callback_friend_request(mTox, friend_request_cb, NULL);
    tox_callback_friend_message(mTox, friend_message_cb, NULL);
    tox_callback_self_connection_status(mTox, self_connection_status_cb, NULL);

    tox_callback_file_chunk_request(mTox, file_chunk_request_cb, NULL);
    tox_callback_file_recv(mTox, file_recv_cb, NULL);
    tox_callback_file_recv_chunk(mTox, file_recv_chunk_cb, NULL);
    tox_callback_file_recv_control(mTox, file_recv_control_cb, NULL);
}

JNIEXPORT void JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_toxKill(JNIEnv *env, jobject thiz) {
//    (*g_jvm)->AttachCurrentThread(g_jvm, Env, NULL);
    tox_kill(mTox);
    (*env)->DeleteGlobalRef(env, g_obj);
//    (*g_jvm)->DestroyJavaVM(g_jvm);
    free(Env);
    free(g_jvm);
    mTox = NULL;
}

/*
** >0 file Send ok
** -1 qlinkNode not valid
** -2 filename not valid
** -3 friendnum not valid
** -4 file open fail
** -5 file send fail
*/
JNIEXPORT jint JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_sendFile(JNIEnv *env, jobject thiz, jstring fileName,
                                                    jstring friendId) {
    //Tox *tox, uint32_t friend_number, uint32_t kind, uint64_t file_size, const uint8_t *file_id,
    //                       const uint8_t *filename, size_t filename_length, TOX_ERR_FILE_SEND *error
    if (mTox != NULL) {
        if (fileName == NULL)
            return -2;
        char *filename = Jstring2CStr(env, fileName);
        LOGD("%s", filename);
        //printf("filename:%s\n",filename);
        if (filename == NULL) {
            return -3;
        }
/*		if (friend_not_valid_Qlink(qlinkNode, friendnum))
		{
        	return -3;
    	}
*/
        if (friendId == NULL) {
            free(filename);
            return -4;
        }
        char *friendId_P = Jstring2CStr(env, friendId);
        if (friendId_P == NULL) {
            free(filename);
            return -5;
        }
        int friendnum = GetFriendNumInFriendlist(friendId_P);
        if (friendnum < 0) {
            free(friendId_P);
            free(filename);
            return -6;
        }

        FILE *tempfile = fopen(filename, "rb");

        if (tempfile == 0) {
            free(friendId_P);
            free(filename);
            return -7;
        }

        fseek(tempfile, 0, SEEK_END);
        uint64_t filesize = ftell(tempfile);
        fseek(tempfile, 0, SEEK_SET);
        LOGD("开始发送文件");
        uint32_t filenum = tox_file_send(mTox, friendnum, TOX_FILE_KIND_DATA, filesize, 0,
                                         (uint8_t *) filename,
                                         strlen(filename), 0);

        if (filenum == -1) {
            free(friendId_P);
            free(filename);
            return -8;
        }
        file_senders[numfilesenders].filesize = filesize;
        file_senders[numfilesenders].file = tempfile;
        file_senders[numfilesenders].friendnum = friendnum;
        file_senders[numfilesenders].filenumber = filenum;
        ++numfilesenders;
        free(filename);
        free(friendId_P);
        return filenum;
    } else {
        return -1;
    }
}

JNIEXPORT jbyteArray JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_sodiumCryptoSeedKeyPair(JNIEnv *env, jobject thiz,
                                                                   jbyteArray publicKey,
                                                                   jbyteArray privateKey,
                                                                   jbyteArray seed) {
    //unsigned char *pk, unsigned char *sk,
    //                            const unsigned char *seed)
    //            __attribute__ ((nonnull)
    jbyte *publicKeyBuffer = (*env)->GetByteArrayElements(env, publicKey, 0);
    jint lenPub = (*env)->GetArrayLength(env, publicKey);
    unsigned char *publicKeyBuf[32];
    unsigned char *puchars[lenPub + 1];
    memset(puchars, 0, lenPub + 1);
    memcpy(puchars, publicKeyBuffer, lenPub);
    puchars[lenPub] = 0;
    (*env)->GetByteArrayRegion(env, publicKey, 0, lenPub, publicKeyBuf);

    jbyte *privateKeyyBuffer = (*env)->GetByteArrayElements(env, privateKey, 0);
    unsigned char *privateKeyBuf[32];
    unsigned char *prchars[lenPub + 1];
    memset(prchars, 0, lenPub + 1);
    memcpy(prchars, publicKeyBuffer, lenPub);
    prchars[lenPub] = 0;
    (*env)->GetByteArrayRegion(env, privateKey, 0, lenPub, privateKeyBuf);

    jbyte *seedBuffer = (*env)->GetByteArrayElements(env, seed, 0);
    jint lenSeed = (*env)->GetArrayLength(env, seed);
    unsigned char *seedBuf[6];
    unsigned char *schars[6 + 1];
    memset(schars, 0, 6 + 1);
    memcpy(schars, publicKeyBuffer, lenPub);
    schars[lenPub] = 0;
    (*env)->GetByteArrayRegion(env, seed, 0, lenSeed, seedBuf);


    int result = crypto_box_seed_keypair((unsigned char *) publicKeyBuf,
                                         (unsigned char *) privateKeyBuf,
                                         (const unsigned char *) seedBuf);

    jbyte *resultByte = (jbyte *) publicKeyBuf;
    jbyteArray jbarray = (*env)->NewByteArray(env, lenPub);
    (*env)->SetByteArrayRegion(env, jbarray, 0, lenPub, resultByte);
    return jbarray;
}


void self_connection_status_cb(Tox *tox, TOX_CONNECTION connection_status, void *user_data) {
    LOGD("自己的状态改变了");
    int friendSize = tox_self_get_friend_list_size(tox);
    LOGD("%d", friendSize);
    print_tox_id(Env, tox);
    switch (connection_status) {
        case TOX_CONNECTION_NONE:
            LOGD("TOX_CONNECTION_NONE");
            show_log(Env, "TOX_CONNECTION_NONE");
            Call_SelfStatusChange_To_Java(Env, 0);
            break;
        case TOX_CONNECTION_TCP:
            show_log(Env, "TOX_CONNECTION_TCP");
            LOGD("TOX_CONNECTION_TCP");
            Call_SelfStatusChange_To_Java(Env, 1);
            break;
        case TOX_CONNECTION_UDP:
            show_log(Env, "TOX_CONNECTION_UDP");
            LOGD("TOX_CONNECTION_UDP");
            Call_SelfStatusChange_To_Java(Env, 2);
            break;
    }
}


int save_data_file(Tox *m, const char *path) {
    data_file_name = path;

    if (save_data(m)) {
        return 1;
    }
    return 0;
}

uint8_t *hex_string_to_bin(const char *hex_string) {
    // byte is represented by exactly 2 hex digits, so lenth of binary string
    // is half of that of the hex one. only hex string with even length
    // valid. the more proper implementation would be to check if strlen(hex_string)
    // is odd and return error code if it is. we assume strlen is even. if it's not
    // then the last byte just won't be written in 'ret'.
    size_t i, len = strlen(hex_string) / 2;
    uint8_t *ret = (uint8_t *) malloc(len);
    const char *pos = hex_string;

    for (i = 0; i < len; ++i, pos += 2) {
        sscanf(pos, "%2hhx", &ret[i]);
    }
    //unsigned char *pk, unsigned char *sk,
    //                            const unsigned char *seed)
    //            __attribute__ ((nonnull)
/*
	printf("%d public key is \n",len);
	for(i=0;i<len;i++)
	{
		printf("%2hhx",ret[i]);
	}
	printf("%d public key is \n",len);
*/
    return ret;
}

static Tox *load_data(void) {
    FILE *data_file = fopen(data_file_name, "r");

    if (data_file) {
        fseek(data_file, 0, SEEK_END);
        size_t size = ftell(data_file);
        rewind(data_file);

        VLA(uint8_t, data, size);

        if (fread(data, sizeof(uint8_t), size, data_file) != size) {
            fputs("[!] could not read data file!\n", stderr);
            fclose(data_file);
            return 0;
        }

        struct Tox_Options options;

        tox_options_default(&options);
        //add by zhijie, disable local discovery,Begin

        //tox_options_set_local_discovery_enabled(&options, false);

        //add by zhijie, disable local discovery,end


        /*Zhijie, add to enable the TCP_relay Begin*/
        //tox_options_set_tcp_port(&options,49734);
        /*Zhijie, add to enable the TCP_relay End*/

        options.savedata_type = TOX_SAVEDATA_TYPE_TOX_SAVE;

        options.savedata_data = data;

        options.udp_enabled = true;
//        options.start_port = 45600;
//        options.end_port = 45700;

        options.savedata_length = size;

        Tox *m = tox_new(&options, NULL);

        if (fclose(data_file) < 0) {
            perror("[!] fclose failed");
            /* we got it open and the expected data read... let it be ok */
            /* return 0; */
        }

        return m;
    }

    return tox_new(NULL, NULL);
}

char *Jstring2CStr(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "utf-8");
    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid,
                                                            strencode); // String .getByte("GB2312");
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte *ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);         //new   char[alen+1]; "\0"
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);  //\u91ca\u653e\u5185\u5b58

    return rtn;
}

void print_tox_id(JNIEnv *env, Tox *tox) {
    LOGD("开始打印toxId");
    uint8_t tox_id_bin[TOX_ADDRESS_SIZE];
    tox_self_get_address(tox, tox_id_bin);
    char tox_id_hex[TOX_ADDRESS_SIZE * 2 + 1];
    sodium_bin2hex(tox_id_hex, sizeof(tox_id_hex), tox_id_bin, sizeof(tox_id_bin));

    for (size_t i = 0; i < sizeof(tox_id_hex) - 1; i++) {
        tox_id_hex[i] = toupper(tox_id_hex[i]);
    }
    show_log(env, tox_id_hex);
    LOGD("Tox ID: %s\n", tox_id_hex);
}


JNIEXPORT jint JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_bootStrap(JNIEnv *env, jobject thiz, jstring address,
                                                     jint jport, jstring publicKey) {
    //Tox *tox, const char *address, uint16_t port, const uint8_t *public_key, TOX_ERR_BOOTSTRAP *error
    char *ipv4 = Jstring2CStr(env, address);
    char *key_string = Jstring2CStr(env, publicKey);
    unsigned char *binary_string = hex_string_to_bin(key_string);
    int result = tox_bootstrap(mTox, ipv4, (uint16_t) jport, binary_string, NULL);
    free(ipv4);
    free(key_string);
    return result;
}

int save_data(Tox *tox) {
    FILE *data_file = fopen(data_file_name, "w");

    if (!data_file) {
        LOGD("文件不存在");

        return 0;
    } else {
        LOGD("data文件存在");
    }
    int res = 1;
    size_t size = tox_get_savedata_size(mTox);
    VLA(uint8_t, data, size);
    tox_get_savedata(tox, data);
    if (fwrite(data, sizeof(uint8_t), size, data_file) != size) {
        fputs("[!] could not write data file (1)!", stderr);
        res = 1;
    }

    if (fclose(data_file) < 0) {
        perror("[!] could not write data file (2)");
        res = 1;
    }
    return res;
}

/**
 * 显示log到androidstudio控制台
 */
void show_log(JNIEnv *env, char *string) {
//    if (env == NULL) {
//        return;
//    }
    JNIEnv *env1;
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &env1, NULL) != JNI_OK) {
        return;
    }
//    jmethodID mid_construct = NULL;
//    jobject jobj = NULL;
    LOGD("log的内容为：%s", string);
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*env1)->FindClass(env1, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return;
    }
//    // 2、获取类的默认构造方法ID
//    mid_construct = (*env)->GetMethodID(env, clazz, "<init>", "()V");
//    if (mid_construct == NULL) {
//        LOGD("找不到默认的构造方法");
//        return;
//    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env1)->GetMethodID(env1, clazz, "showLog", "(Ljava/lang/String;)V");
//    //创建该类的实例
//    jobj = (*env)->NewObject(env, clazz, mid_construct);
//    if (jobj == NULL) {
//        LOGD("在com/stratagile/tox/toxcore/ToxCoreJni类中找不到showLog方法");
//        return;
//    }
    jstring callbackStr = (*env1)->NewStringUTF(env1, string);
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*env1)->CallVoidMethod(env1, g_obj, javaCallback, callbackStr);

    (*env1)->DeleteLocalRef(env1, clazz);
    (*env1)->DeleteLocalRef(env1, callbackStr);
//    (*g_jvm)->DetachCurrentThread(g_jvm);
}

/* Call_SelfStatusChange_To_Java
** when self status change ,call java func
** 0 success call java func
** -1 can't find  class
** -2 can't find  mid_construct
** -3 can't find mid_instance ID
** -4 can't Create an instance
*/
int Call_SelfStatusChange_To_Java(JNIEnv *env, int status) {
//    jmethodID mid_construct = NULL;
//    jobject jobj = NULL;
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
        return -1;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*env)->FindClass(env, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return 0;
    }
//    // 2、获取类的默认构造方法ID
//    mid_construct = (*env)->GetMethodID(env, clazz, "<init>", "()V");
//    if (mid_construct == NULL) {
//        LOGD("找不到默认的构造方法");
//        return 0;
//    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env)->GetMethodID(env, clazz, "callSelfChange", "(I)V");
//    //创建该类的实例
//    jobj = (*env)->NewObject(env, clazz, mid_construct);
//    if (jobj == NULL) {
//        LOGD("在com/stratagile/tox/toxcore/ToxCoreJni类中找不到showLog方法");
//        return 0;
//    }
    LOGD("开始调用java方法");

    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*env)->CallVoidMethod(env, g_obj, javaCallback, status);

    (*env)->DeleteLocalRef(env, clazz);
//    (*Env)->DeleteLocalRef(env,callbackStr);
//    (*g_jvm)->DetachCurrentThread(g_jvm);
    return 0;
}


JNIEXPORT void JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_getToxStatus(JNIEnv *env, jobject thiz) {
    TOX_CONNECTION connection_status = tox_self_get_connection_status(mTox);
    print_tox_id(env, mTox);
    switch (connection_status) {
        case TOX_CONNECTION_NONE:
            LOGD("TOX_CONNECTION_NONE");
            show_log(env, "TOX_CONNECTION_NONE");
            Call_SelfStatusChange_To_Java(env, 0);
            break;
        case TOX_CONNECTION_TCP:
            show_log(env, "TOX_CONNECTION_TCP");
            LOGD("TOX_CONNECTION_TCP");
            Call_SelfStatusChange_To_Java(env, 1);
            break;
        case TOX_CONNECTION_UDP:
            show_log(env, "TOX_CONNECTION_UDP");
            LOGD("TOX_CONNECTION_UDP");
            Call_SelfStatusChange_To_Java(env, 2);
            break;
    }
}

JNIEXPORT jint JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_addFriend(JNIEnv *env, jobject thiz, jstring friendid) {
    if (mTox != NULL) { // add friend command: /f ID
        int i, delta = 0;
        if (friendid == NULL)
            return -2;
        char *friendid_p = Jstring2CStr(env, friendid);
        if (friendid_p == NULL)
            return -3;
        int friendNum = GetFriendNumInFriendlist(friendid_p);
        LOGD("friendNum为：%d", friendNum);
        if (friendNum >= 0) {
//            int res = tox_friend_delete(mTox, friendNum, NULL);
//            if (res) {
//                printf("remove a friend success\n");
//            } else {
//                printf("remove a friend fail\n");
//            }
            return -1;
        }
        TOX_ERR_FRIEND_ADD error;
        unsigned char *bin_string = hex_string_to_bin(friendid_p);

        int result = tox_friend_add(mTox, bin_string, (const uint8_t *) "Hi PPM", sizeof("Hi PPM"),
                                    &error);
        char numstring[100];

        switch (error) {
            case TOX_ERR_FRIEND_ADD_TOO_LONG:
                LOGD("[i] Message is too long.");
                break;

            case TOX_ERR_FRIEND_ADD_NO_MESSAGE:
                LOGD("[i] Please add a message to your request.");
                break;

            case TOX_ERR_FRIEND_ADD_OWN_KEY:
                LOGD("[i] That appears to be your own ID.");
                break;

            case TOX_ERR_FRIEND_ADD_ALREADY_SENT:
                LOGD("[i] Friend request already sent.delete friend and add ");
                break;

            case TOX_ERR_FRIEND_ADD_BAD_CHECKSUM:
                LOGD("[i] Address has a bad checksum.");
                break;

            case TOX_ERR_FRIEND_ADD_SET_NEW_NOSPAM:
                LOGD("[i] New nospam set.");
                break;

            case TOX_ERR_FRIEND_ADD_MALLOC:
                LOGD("[i] malloc error.");
                break;

            case TOX_ERR_FRIEND_ADD_NULL:
                LOGD("[i] message was NULL.");
                break;

            case TOX_ERR_FRIEND_ADD_OK:
                LOGD("[i] Added friend as %d.", result);
                //save_data(qlinkNode);
                break;
        }
        free(friendid_p);
        free(bin_string);
        return result;
    }
}

JNIEXPORT jint JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_deleteFriend(JNIEnv *env, jobject thiz,
                                                        jstring friendid) {
    if (friendid == NULL)
        return -2;
    char *friendid_p = Jstring2CStr(env, friendid);
    if (friendid_p == NULL) {
        return -3;
    }
    return tox_friend_delete(mTox, friendid_p, NULL);
}

JNIEXPORT void Java_com_stratagile_tox_toxcore_ToxCoreJni_iterate(JNIEnv *env, jobject thiz) {
    while (1) {
        tox_iterate(mTox);
        usleep(tox_iteration_interval(mTox) * 1000);
    }
}

/**
 * 发送消息
 * 返回-2 friendid_p为null
 * 返回-1 好友不存在
 */
JNIEXPORT jint JNICALL
Java_com_stratagile_tox_toxcore_ToxCoreJni_sendMessage(JNIEnv *env, jobject thiz, jstring message,
                                                       jstring friendId) {
    if (friendId == NULL)
        return -2;
    char *friendid_p = Jstring2CStr(env, friendId);
    if (friendid_p == NULL)
        return -3;
    char *message_p = Jstring2CStr(env, message);
    int friendNum = GetFriendNumInFriendlist(friendid_p);
    //Tox *tox, uint32_t friend_number, TOX_MESSAGE_TYPE type, const uint8_t *message,
    //                                 size_t length, TOX_ERR_FRIEND_SEND_MESSAGE *error
    LOGD("%s", friendid_p);
    LOGD("要发送的消息：%s", message_p);
    LOGD("%d", tox_self_get_friend_list_size(mTox));
//    uint32_t *list;
//    tox_self_get_friend_list(mTox, list);
//    LOGD("第一个 %s", (char*)list[0]);
    if (friendNum < 0) {
        LOGD("friendNum= %d", friendNum);
        return -1;
    }
    TOX_ERR_FRIEND_SEND_MESSAGE error;
    int result = tox_friend_send_message(mTox, friendNum, TOX_MESSAGE_TYPE_NORMAL, message_p,
                                         strlen(message_p), &error);
    switch (error) {
        case TOX_ERR_FRIEND_SEND_MESSAGE_NULL:
            LOGD("TOX_ERR_FRIEND_SEND_MESSAGE_NULL");
            break;
        case TOX_ERR_FRIEND_SEND_MESSAGE_EMPTY:
            LOGD("TOX_ERR_FRIEND_SEND_MESSAGE_EMPTY");
            break;
        case TOX_ERR_FRIEND_SEND_MESSAGE_FRIEND_NOT_FOUND:
            LOGD("TOX_ERR_FRIEND_SEND_MESSAGE_FRIEND_NOT_FOUND");
            break;
        default:
            break;
    }
    return result;
}

/**
 * 对方请求加好友的处理，自动添加为好友
 */
void friend_request_cb(Tox *m, const uint8_t *public_key, const uint8_t *data, size_t length,
                       void *userdata) {
    ////new_lines("[i] auto_accept_request received friend request with message:");
    ////new_lines((const char *)data);
    LOGD("收到了tox的好友请求");
    char numchar[150];
    uint8_t fraddr_bin[TOX_ADDRESS_SIZE];
    char fraddr_str[FRADDR_TOSTR_BUFSIZE];
    sprintf(numchar, "[i] auto_accept_request accept request with /a %u", num_requests);
    // //new_lines(numchar);
    {
        uint32_t num = tox_friend_add_norequest(m, public_key, NULL);
        if (tox_friend_get_public_key(m, num, fraddr_bin, NULL)) {
            fraddr_to_str(fraddr_bin, fraddr_str);
        }

        if (num != UINT32_MAX) {
            sprintf(numchar, "[i] friend request %s accepted as friend no. %d", fraddr_str, num);
            ////new_lines(numchar);
            /*           if(m!=NULL)
                       {
                           save_data(m);
                       }*/
        } else {
            sprintf(numchar, "[i] failed to add friend");
            // //new_lines(numchar);
        }
    }
//    save_data(mTox);
    //do_refresh();
}

/**
 * 接收到好友的消息
 */
void friend_message_cb(Tox *m, uint32_t friendnumber, TOX_MESSAGE_TYPE type, const uint8_t *string,
                       size_t length, void *userdata) {
    /* ensure null termination */
    LOGD("收到消息了");
    LOGD("friendnumber= %d", friendnumber);
//    tox_friend_send_message(m, friendnumber, type, string, length, NULL);
    VLA(uint8_t, null_string, length + 1);
    memcpy(null_string, string, length);
//    LOGD((const char*) null_string);
    LOGD("%s", (char *) null_string);
    null_string[length] = 0;
    print_formatted_message(m, (char *) null_string, friendnumber, 0);
}

/**
 * 好友连接状态的回调
 */
void friend_connection_status_cb(Tox *tox, uint32_t friend_number, TOX_CONNECTION connection_status,
                                 void *user_data) {
    LOGD("好友状态改变");
    char fraddr_str[FRAPUKKEY_TOSTR_BUFSIZE];
    uint8_t fraddr_bin[TOX_PUBLIC_KEY_SIZE];
    if (tox_friend_get_public_key(tox, friend_number, fraddr_bin, NULL)) {
        frpuk_to_str(fraddr_bin, fraddr_str);
    }
    int status = 0;
    switch (connection_status) {
        case TOX_CONNECTION_NONE:
            status = 0;
            break;
        case TOX_CONNECTION_TCP:
            status = 1;
            break;
        case TOX_CONNECTION_UDP:
            status = 2;
            break;
    }
    friend_status_callback(Env, status, fraddr_str);
    LOGD("%s", (char *) fraddr_str);
}

/**
 * 发送方发送之前
 */
void file_recv_control_cb(Tox *tox, uint32_t friend_number, uint32_t file_number, TOX_FILE_CONTROL control, void *user_data) {
    LOGD("file_recv_control_cb");
    char msg[512] = {0};
    //  sprintf(msg, "[t] control %u received", control);
    // printf("[t] control %u received", control);
    //new_lines(msg);
    call_java_start_send_file(friend_number, file_number);
    if (control == TOX_FILE_CONTROL_CANCEL) {
        unsigned int i;
        for (i = 0; i < NUM_FILE_SENDERS; ++i) {
            /* This is slow */
            if (file_senders[i].file && file_senders[i].friendnum == friend_number && file_senders[i].filenumber == file_number) {
                fclose(file_senders[i].file);
                file_senders[i].file = 0;
                LOGD("[t] %u file transfer: %u cancelled", file_senders[i].friendnum, file_senders[i].filenumber);
                //new_lines(msg);
            }
        }
    }
}

/**
 * 发送方的片段
 *
 */
void
file_chunk_request_cb(Tox *tox, uint32_t friend_number, uint32_t file_number, uint64_t position, size_t length, void *user_data) {
//    LOGD("file_chunk_request_cb");
    unsigned int i;

    for (i = 0; i < NUM_FILE_SENDERS; ++i) {
        /* This is slow */
        if (file_senders[i].file && file_senders[i].friendnum == friend_number && file_senders[i].filenumber == file_number) {
            if (length == 0) {
                fclose(file_senders[i].file);
                file_senders[i].file = 0;
                call_java_sendfile_rate((int) position, (int) file_senders[0].filesize);
                LOGD("[t] %u file transfer: %u completed", file_senders[i].friendnum, file_senders[i].filenumber);
                //new_lines(msg);
                break;
            }

            fseek(file_senders[i].file, position, SEEK_SET);
            VLA(uint8_t, data, length);
            int len = fread(data, 1, length, file_senders[i].file);
            tox_file_send_chunk(tox, friend_number, file_number, position, data, len, 0);
            call_java_sendfile_rate((int) position, (int) file_senders[0].filesize);
            break;
        }
    }
}

/**
 * 开始发送文件，回调给java
 */
void call_java_start_send_file(int friendNumber, int fileNumber) {
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &Env, NULL) != JNI_OK) {
        return;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*Env)->FindClass(Env, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return;
    }
    char fraddr_str[FRAPUKKEY_TOSTR_BUFSIZE];
    uint8_t fraddr_bin[TOX_PUBLIC_KEY_SIZE];

    if (tox_friend_get_public_key(mTox, (uint32_t) friendNumber, fraddr_bin, NULL)) {
        frpuk_to_str(fraddr_bin, fraddr_str);
    }
    jstring jFriendId = (*Env)->NewStringUTF(Env, fraddr_str);
    //找到需要调用的方法ID
    jmethodID javaCallback = (*Env)->GetMethodID(Env, clazz, "starSendFile", "(ILjava/lang/String;)V");
    LOGD("开始调用java方法");
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*Env)->CallVoidMethod(Env, g_obj, javaCallback, fileNumber, jFriendId);
    (*Env)->DeleteLocalRef(Env, clazz);
    (*Env)->DeleteLocalRef(Env, jFriendId);
}

/**
 * 回调给java开始接收文件
 */
void call_java_start_receive_file(int freindNumber, int fileNumber, char *fileName) {
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &Env, NULL) != JNI_OK) {
        return;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*Env)->FindClass(Env, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return;
    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*Env)->GetMethodID(Env, clazz, "startReceiveFile", "(ILjava/lang/String;Ljava/lang/String;)V");
    LOGD("开始调用java方法");
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    jstring jFileName = (*Env)->NewStringUTF(Env, fileName);
    char fraddr_str[FRAPUKKEY_TOSTR_BUFSIZE];
    uint8_t fraddr_bin[TOX_PUBLIC_KEY_SIZE];

    if (tox_friend_get_public_key(mTox, freindNumber, fraddr_bin, NULL)) {
        frpuk_to_str(fraddr_bin, fraddr_str);
    }
    jstring jFriendId = (*Env)->NewStringUTF(Env, fraddr_str);
    (*Env)->CallVoidMethod(Env, g_obj, javaCallback, fileNumber, jFileName, jFriendId);
    (*Env)->DeleteLocalRef(Env, clazz);
    (*Env)->DeleteLocalRef(Env, jFileName);
    free(fraddr_str);
    free(fraddr_bin);
}

/**
 * 回调给java发送了文件的多少字节
 */
void call_java_sendfile_rate(int position, int filesize) {
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &Env, NULL) != JNI_OK) {
        return;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*Env)->FindClass(Env, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return;
    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*Env)->GetMethodID(Env, clazz, "sendFileRate", "(II)V");
    LOGD("开始调用java方法");
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*Env)->CallVoidMethod(Env, g_obj, javaCallback, position, filesize);
    (*Env)->DeleteLocalRef(Env, clazz);
}
/**
 * 回调给java接收了文件的多少字节
 */
void call_java_receivedfile_rate(int friendNumber, int position, int filesize) {
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &Env, NULL) != JNI_OK) {
        return;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*Env)->FindClass(Env, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return;
    }
    char fraddr_str[FRAPUKKEY_TOSTR_BUFSIZE];
    uint8_t fraddr_bin[TOX_PUBLIC_KEY_SIZE];

    if (tox_friend_get_public_key(mTox, friendNumber, fraddr_bin, NULL)) {
        frpuk_to_str(fraddr_bin, fraddr_str);
    }
    jstring jFriendId = (*Env)->NewStringUTF(Env, fraddr_str);
    //找到需要调用的方法ID
    jmethodID javaCallback = (*Env)->GetMethodID(Env, clazz, "receivedFileRate", "(IILjava/lang/String;)V");
    LOGD("开始调用java方法");
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*Env)->CallVoidMethod(Env, g_obj, javaCallback, position, filesize, jFriendId);
    (*Env)->DeleteLocalRef(Env, clazz);
    (*Env)->DeleteLocalRef(Env, jFriendId);
}



/**
 * 接收方的片段
 */
void file_recv_chunk_cb(Tox *tox, uint32_t friend_number, uint32_t file_number, uint64_t position, const uint8_t *data, size_t length, void *user_data) {
    LOGD("file_recv_chunk_cb");
    if (length == 0) {
        char msg[512];
        //sprintf(msg, "[t] %u file transfer: %u completed", friendnumber, filenumber);
        //new_lines(msg);
        printf("file %s transfer from friendnumber %u completed\n" ,recv_filename, friend_number);
        LOGD("文件传输完毕");
        call_java_receivedfile_rate(friend_number, (int) position, (int) received_file_size);
//        Call_File_Process_Func_From_Java(recv_filename, recv_filesize,friend_number);
        return;
    }

//    char filename[256];
//    sprintf(filename, "%u.%u.bin", friendnumber, filenumber);
    /*20180129,wenchao,use default filename,begin*/
    FILE *pFile = fopen(recv_filename, "r+b");
    /*20180129,wenchao,use default filename,end*/

    if (pFile == NULL) {
        pFile = fopen(recv_filename, "wb");
    }

    fseek(pFile, position, SEEK_SET);

    if (fwrite(data, length, 1, pFile) != 1) {
        //new_lines("Error writing to file");
        printf("Error writing to file\n");
    }
    call_java_receivedfile_rate(friend_number, (int) position, (int) received_file_size);
    fclose(pFile);
}

/**
 * 接收方接收之前
 */
void file_recv_cb(Tox *tox, uint32_t friend_number, uint32_t file_number, uint32_t kind,
                  uint64_t file_size, const uint8_t *filename, size_t filename_length,
                  void *user_data) {
    LOGD("file_recv_cb");
    received_file_size = file_size;
    if (filename != NULL) {
        memset(recv_filename, 0x00, 200);
//        Call_GetFilePathFromJava(recv_filename,filename);
        strcat(recv_filename, filename);
        if (recv_filename == NULL)
            return;
    }
    recv_filesize = (int) file_size;
    if (kind != TOX_FILE_KIND_DATA) {
        //new_lines("Refused invalid file type.");
        printf("Refused invalid file type.\n");
        tox_file_control(tox, friend_number, file_number, TOX_FILE_CONTROL_CANCEL, 0);
        return;
    }

    char msg[512];
    // sprintf(msg, "[t] %u is sending us: %s of size %llu", friend_number, filename, (long long unsigned int)file_size);
    printf("friend_number: %u is sending us: %s of size %llu\n", friend_number, filename,
           (long long unsigned int) file_size);
    //new_lines(msg);

    if (tox_file_control(tox, friend_number, file_number, TOX_FILE_CONTROL_RESUME, 0)) {
        //  sprintf(msg, "Accepted file transfer. (saving file as: %u.%u.bin)", friend_number, file_number);
        // printf("Accepted file transfer. (saving file as: %s)\n", recv_filename);
        //new_lines(msg);
        call_java_start_receive_file(friend_number, file_number, filename);
    } else {
        //new_lines("Could not accept file transfer.");
        printf("Could not accept file transfer.");
    }
}

void frpuk_to_str(uint8_t *id_bin, char *id_str) {
    uint32_t i, delta = 0, pos_extra = 0, sum_extra = 0;

    for (i = 0; i < TOX_PUBLIC_KEY_SIZE; i++) {
        sprintf(&id_str[2 * i + delta], "%02hhX", id_bin[i]);

        if ((i + 1) == TOX_PUBLIC_KEY_SIZE) {
            pos_extra = 2 * (i + 1) + delta;
        }

        if (i >= TOX_PUBLIC_KEY_SIZE) {
            sum_extra |= id_bin[i];
        }

/*
        if (!((i + 1) % FRADDR_TOSTR_CHUNK_LEN)) {
            id_str[2 * (i + 1) + delta] = ' ';
            delta++;
        }
        */
    }

    id_str[2 * i + delta] = 0;

    if (!sum_extra) {
        id_str[pos_extra] = 0;
    }
}

/**
 * 接受到消息的处理
 */
void friend_status_callback(JNIEnv *env, int status, char *friendNumber) {
//    jmethodID mid_construct = NULL;
//    jobject jobj = NULL;

    if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
        return;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*env)->FindClass(env, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return;
    }
//    // 2、获取类的默认构造方法ID
//    mid_construct = (*env)->GetMethodID(env, clazz, "<init>", "()V");
//    if (mid_construct == NULL) {
//        LOGD("找不到默认的构造方法");
//        return;
//    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env)->GetMethodID(env, clazz, "freindStatus",
                                                 "(Ljava/lang/String;I)V");
//    //创建该类的实例
//    jobj = (*env)->NewObject(env, clazz, mid_construct);
//    if (jobj == NULL) {
//        LOGD("在com/stratagile/tox/toxcore/ToxCoreJni类中找不到freindStatus方法");
//        return;
//    }
    LOGD("开始调用java方法");
    jstring jfriendNumber = (*env)->NewStringUTF(env, friendNumber);
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*env)->CallVoidMethod(env, g_obj, javaCallback, jfriendNumber, status);

    (*env)->DeleteLocalRef(env, clazz);
    (*env)->DeleteLocalRef(env, jfriendNumber);
//    (*g_jvm)->DetachCurrentThread(g_jvm);
}

void fraddr_to_str(uint8_t *id_bin, char *id_str) {
    uint32_t i, delta = 0, pos_extra = 0, sum_extra = 0;

    for (i = 0; i < TOX_ADDRESS_SIZE; i++) {
        sprintf(&id_str[2 * i + delta], "%02hhX", id_bin[i]);

        if ((i + 1) == TOX_PUBLIC_KEY_SIZE) {
            pos_extra = 2 * (i + 1) + delta;
        }

        if (i >= TOX_PUBLIC_KEY_SIZE) {
            sum_extra |= id_bin[i];
        }

/*
        if (!((i + 1) % FRADDR_TOSTR_CHUNK_LEN)) {
            id_str[2 * (i + 1) + delta] = ' ';
            delta++;
        }
        */
    }

    id_str[2 * i + delta] = 0;

    if (!sum_extra) {
        id_str[pos_extra] = 0;
    }
}

void print_formatted_message(Tox *m, char *message, uint32_t friendnum, uint8_t outgoing) {
    char name[TOX_MAX_NAME_LENGTH + 1];
//    getfriendname_terminated(m, friendnum, name);
    VLA(char, msg, 100 + strlen(message) + strlen(name) + 1);
    time_t rawtime;
    struct tm *timeinfo;
    time(&rawtime);
    timeinfo = localtime(&rawtime);
    char fraddr_str[FRAPUKKEY_TOSTR_BUFSIZE];
    uint8_t fraddr_bin[TOX_PUBLIC_KEY_SIZE];

    if (tox_friend_get_public_key(m, friendnum, fraddr_bin, NULL)) {
        frpuk_to_str(fraddr_bin, fraddr_str);
//        publickey = (*env)->NewStringUTF(env,fraddr_str);
    }
    show_log(Env, message);
    LOGD("好友的toxId为： %s", fraddr_str);
    received_message(Env, message, fraddr_str);
    /* assume that printing the date once a day is enough */
//    if (fmtmsg_tm_mday != timeinfo->tm_mday) {
//        fmtmsg_tm_mday = timeinfo->tm_mday;
//
//
//        // strftime(msg, 100, "Today is %a %b %d %Y.", timeinfo);
//        /* %x is the locale's preferred date format */
//        // strftime(msg, 100, "Today is %x.", timeinfo);
//        strftime(msg,100,"Time:%Y-%m-%d %H:%M:%S",timeinfo);
//        ////new_lines(msg);
//    }

    char time[64];
    //strftime(time, 64, "%I:%M:%S %p", timeinfo);
    /* %X is the locale's preferred time format */
    // strftime(time, 64, "%X", timeinfo);

    strftime(time, 64, "Time:%Y-%m-%d %H:%M:%S", timeinfo);

    if (outgoing) {
        /* tgt: friend */
        sprintf(msg, "[%d] %s =>{%s} %s", friendnum, time, name, message);
    } else {
        /* src: friend */
        sprintf(msg, "[%d] %s <%s>: %s", friendnum, time, name, message);
    }

    // //new_lines(msg);
    char *pass = NULL;

    ////new_lines(message);
//	        LOGD(message);
    printf("\n %s %s %s", name, time, message);
}

/**
 * 接受到消息的处理
 */
void received_message(JNIEnv *env, char *string, char *friendNumber) {
//    jmethodID mid_construct = NULL;
//    jobject jobj = NULL;

    if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
        return;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*env)->FindClass(env, "com/stratagile/tox/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/tox/toxcore/ToxCoreJni'这个类");
        return;
    }
//    // 2、获取类的默认构造方法ID
//    mid_construct = (*env)->GetMethodID(env, clazz, "<init>", "()V");
//    if (mid_construct == NULL) {
//        LOGD("找不到默认的构造方法");
//        return;
//    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env)->GetMethodID(env, clazz, "receivedMessage",
                                                 "(Ljava/lang/String;Ljava/lang/String;)V");
//    //创建该类的实例
//    jobj = (*env)->NewObject(env, clazz, mid_construct);
//    if (jobj == NULL) {
//        LOGD("在com/stratagile/tox/toxcore/ToxCoreJni类中找不到receivedMessage方法");
//        return;
//    }
    LOGD("开始调用java方法");
    jstring callbackStr = (*env)->NewStringUTF(env, string);
    jstring jfriendNumber = (*env)->NewStringUTF(env, friendNumber);
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*env)->CallVoidMethod(env, g_obj, javaCallback, jfriendNumber, callbackStr);

    (*env)->DeleteLocalRef(env, clazz);
    (*env)->DeleteLocalRef(env, callbackStr);
    (*env)->DeleteLocalRef(env, jfriendNumber);
//    (*g_jvm)->DetachCurrentThread(g_jvm);
}

/* GetFriendNumInFriendlist
** Input the friend ID and get the friend num back
** After the app get the friend p2p ID from the block chain, app may call this function the get the friendnum
** The friend num may be quite useful in the other function
** -1 qlinkNode is not valid
** -2 invalid input friendId
** -3 hex_string_to_bin fail
** -4 friend not in list
** >=0 the friend num
*/
int GetFriendNumInFriendlist(uint8_t *friendId_P) {

    if (mTox != NULL) {
        if (friendId_P == NULL) {
            return -2;
        }
        char *friendId_bin = hex_string_to_bin(friendId_P);
        if (friendId_bin == NULL)
            return -3;
        int friendLoc = tox_friend_get_Num_in_friendlist(mTox, friendId_bin, NULL);
        //printf("This friend loc is %d\n", friendLoc);

        free(friendId_bin);
        if (friendLoc == -1) {
            return -4;
        } else
            return friendLoc;
    }
    return -1;

}

void deleteFriendAll() {
    int friendcounts = tox_self_get_friend_list_size(mTox);
    int i;
    for (i = 0; i < friendcounts; i++) {
        int res = tox_friend_delete(mTox, friendcounts - 1 - i, NULL);
        if (res) {
            printf("remove a friend success\n");
            //save_data(qlinkNode);
        } else {
            printf("remove a friend fail\n");
        }
    }
}




