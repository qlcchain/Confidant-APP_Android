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

#define NUM_FILE_SENDERS 64
typedef struct {
    FILE *file;
    uint32_t friendnum;
    uint32_t filenumber;
} File_Sender;
static File_Sender file_senders[NUM_FILE_SENDERS];
static uint8_t numfilesenders;

Tox *mTox = NULL;
JNIEnv *Env;
JavaVM *g_jvm = NULL;
jobject g_obj = NULL;


#define LOG_TAG  "C_TAG"
#define LOGD(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define FRADDR_TOSTR_CHUNK_LEN 8

static const char *data_file_name = NULL;
char dataPathFile[200] = {0};
const char *savedata_filename = "savedata.tox";

#define FRAPUKKEY_TOSTR_BUFSIZE (TOX_PUBLIC_KEY_SIZE * 2 + 1)

#define FRADDR_TOSTR_BUFSIZE (TOX_ADDRESS_SIZE * 2 + TOX_ADDRESS_SIZE / FRADDR_TOSTR_CHUNK_LEN + 1)
tox_self_connection_status_cb self_connection_status_cb;
tox_friend_request_cb friend_request_cb;
tox_friend_message_cb friend_message_cb;
tox_friend_connection_status_cb friend_connection_status_cb;

JNIEXPORT void JNICALL
Java_com_stratagile_toxcore_ToxCoreJni_createTox(JNIEnv *env, jobject thiz, jstring dataPath) {
    Env = env;
    (*env)->GetJavaVM(env, &g_jvm);
    //不能直接赋值(g_obj = obj)
    g_obj = (*env)->NewGlobalRef(env, thiz);

    char *dataPath_p = Jstring2CStr(env, dataPath);
    // strcat 将两个char连接起来
    data_file_name = strcat(dataPath_p, savedata_filename);
    //strcpy 字符串复制
    strcpy(dataPathFile, dataPath_p);
    mTox = load_data();
    const char *name = "ppm Tox";
    tox_self_set_name(mTox, name, strlen(name), NULL);
    const char *status_message = "ppm your messages";
    save_data_file(mTox, data_file_name);
//    deleteFriendAll();
    tox_self_set_status_message(mTox, status_message, strlen(status_message), NULL);
    tox_callback_friend_connection_status(mTox, friend_connection_status_cb, NULL);
    tox_callback_friend_request(mTox, friend_request_cb, NULL);
    tox_callback_friend_message(mTox, friend_message_cb, NULL);
    tox_callback_self_connection_status(mTox, self_connection_status_cb, NULL);
}

JNIEXPORT jint JNICALL
Java_com_stratagile_toxcore_ToxCoreJni_sendFile(JNIEnv *env, jobject thiz, jstring fileName, jstring friendId) {
    //Tox *tox, uint32_t friend_number, uint32_t kind, uint64_t file_size, const uint8_t *file_id,
    //                       const uint8_t *filename, size_t filename_length, TOX_ERR_FILE_SEND *error
    return 0;
}
JNIEXPORT jbyteArray JNICALL
Java_com_stratagile_toxcore_ToxCoreJni_sodiumCryptoSeedKeyPair(JNIEnv *env, jobject thiz, jbyteArray publicKey, jbyteArray privateKey, jbyteArray seed) {
    //unsigned char *pk, unsigned char *sk,
    //                            const unsigned char *seed)
    //            __attribute__ ((nonnull)
    jbyte* publicKeyBuffer = (*env)->GetByteArrayElements(env,publicKey,0);
    unsigned char* publicKeyBuf=(unsigned char*)publicKeyBuffer;

    jbyte* privateKeyyBuffer = (*env)->GetByteArrayElements(env,privateKey,0);
    unsigned char* privateKeyBuf=(unsigned char*)privateKeyyBuffer;

    jbyte* seedBuffer = (*env)->GetByteArrayElements(env,seed,0);
    unsigned char* seedBuf=(unsigned char*)seedBuffer;
    int result = crypto_box_seed_keypair(publicKeyBuf, privateKeyBuf, seedBuf);
    LOGD("%s", publicKeyBuf);
    LOGD("%s", privateKeyBuf);
    jbyte* resultByte = (jbyte*)publicKeyBuf;
    jint len = (*env)->GetArrayLength(env, publicKey);
    jbyteArray  jbarray = (*env)-> NewByteArray(env,len);
    (*env)->SetByteArrayRegion(env,jbarray, 0, len, resultByte);
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
        case TOX_CONNECTION_UDP:
            show_log(Env, "TOX_CONNECTION_UDP");
            LOGD("TOX_CONNECTION_UDP");
            Call_SelfStatusChange_To_Java(Env, 2);
            break;
        case TOX_CONNECTION_TCP:
            show_log(Env, "TOX_CONNECTION_TCP");
            LOGD("TOX_CONNECTION_TCP");
            Call_SelfStatusChange_To_Java(Env, 1);
            break;
    }
}

JNIEXPORT void JNICALL Java_com_stratagile_toxcore_ToxCoreJni_toxKill(JNIEnv *env, jobject thiz) {
    free(Env);
    free(g_jvm);
    free(g_obj);
    tox_kill(mTox);
    mTox = NULL;
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
Java_com_stratagile_toxcore_ToxCoreJni_bootStrap(JNIEnv *env, jobject thiz, jstring address,
                                                 jstring jport, jstring publicKey) {
    //Tox *tox, const char *address, uint16_t port, const uint8_t *public_key, TOX_ERR_BOOTSTRAP *error
    char *ipv4 = Jstring2CStr(env, address);
    char *key_string = Jstring2CStr(env, publicKey);
    uint16_t port = 33445;
//    sodium_hex2bin(dht_node.key_bin, sizeof(dht_node.key_bin), dht_node.key_hex, sizeof(dht_node.key_hex)-1, NULL, NULL, NULL);
    unsigned char *binary_string = hex_string_to_bin(key_string);
    int result = tox_bootstrap(mTox, ipv4, port, binary_string, NULL);
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
    if (env == NULL) {
        return;
    }
    if((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL)  !=  JNI_OK) {
        return;
    }
    jmethodID mid_construct = NULL;
    jobject jobj = NULL;
    LOGD("log的内容为：%s", string);
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*env)->FindClass(env, "com/stratagile/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/toxcore/ToxCoreJni'这个类");
        return;
    }
    // 2、获取类的默认构造方法ID
    mid_construct = (*env)->GetMethodID(env, clazz, "<init>", "()V");
    if (mid_construct == NULL) {
        LOGD("找不到默认的构造方法");
        return;
    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env)->GetMethodID(env, clazz, "showLog", "(Ljava/lang/String;)V");
    //创建该类的实例
    jobj = (*env)->NewObject(env, clazz, mid_construct);
    if (jobj == NULL) {
        LOGD("在com/stratagile/toxcore/ToxCoreJni类中找不到showLog方法");
        return;
    }
    jstring callbackStr = (*env)->NewStringUTF(env, string);
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*env)->CallVoidMethod(env, jobj, javaCallback, callbackStr);

    (*env)->DeleteLocalRef(env, clazz);
    (*env)->DeleteLocalRef(env, jobj);
    (*env)->DeleteLocalRef(env, callbackStr);
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
    jmethodID mid_construct = NULL;
    jobject jobj = NULL;
    if((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL)  !=  JNI_OK) {
        return -1;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*env)->FindClass(env, "com/stratagile/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/toxcore/ToxCoreJni'这个类");
        return 0;
    }
    // 2、获取类的默认构造方法ID
    mid_construct = (*env)->GetMethodID(env, clazz, "<init>", "()V");
    if (mid_construct == NULL) {
        LOGD("找不到默认的构造方法");
        return 0;
    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env)->GetMethodID(env, clazz, "callSelfChange", "(I)V");
    //创建该类的实例
    jobj = (*env)->NewObject(env, clazz, mid_construct);
    if (jobj == NULL) {
        LOGD("在com/stratagile/toxcore/ToxCoreJni类中找不到showLog方法");
        return 0;
    }
    LOGD("开始调用java方法");

    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*env)->CallVoidMethod(env, jobj, javaCallback, status);

    (*env)->DeleteLocalRef(env, clazz);
    (*env)->DeleteLocalRef(env, jobj);
//    (*env)->DeleteLocalRef(env,callbackStr);
    return 0;
}


JNIEXPORT void JNICALL Java_com_stratagile_toxcore_ToxCoreJni_getToxStatus(JNIEnv *env, jobject thiz) {
    TOX_CONNECTION connection_status = tox_self_get_connection_status(mTox);
    print_tox_id(env, mTox);
    switch (connection_status) {
        case TOX_CONNECTION_NONE:
            LOGD("TOX_CONNECTION_NONE");
            show_log(env, "TOX_CONNECTION_NONE");
            Call_SelfStatusChange_To_Java(env, 0);
            break;
        case TOX_CONNECTION_UDP:
            show_log(env, "TOX_CONNECTION_UDP");
            LOGD("TOX_CONNECTION_UDP");
            Call_SelfStatusChange_To_Java(env, 2);
            break;
        case TOX_CONNECTION_TCP:
            show_log(env, "TOX_CONNECTION_TCP");
            LOGD("TOX_CONNECTION_TCP");
            Call_SelfStatusChange_To_Java(env, 1);
            break;
    }
}

JNIEXPORT jint JNICALL
Java_com_stratagile_toxcore_ToxCoreJni_addFriend(JNIEnv *env, jobject thiz, jstring friendid) {
    if (mTox != NULL) { // add friend command: /f ID
        int i, delta = 0;
        if (friendid == NULL)
            return -2;
        char *friendid_p = Jstring2CStr(env, friendid);
        if (friendid_p == NULL)
            return -3;
        int friendNum = GetFriendNumInFriendlist(friendid_p);
        LOGD("friendNum为：%d", friendNum);
        if (friendNum > 0) {
            return friendNum;
        }
        TOX_ERR_FRIEND_ADD error;
        unsigned char *bin_string = hex_string_to_bin(friendid_p);
        int result =  tox_friend_add(mTox, bin_string, (const uint8_t *) "Hi PPM", sizeof("Hi PPM"), &error);
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
Java_com_stratagile_toxcore_ToxCoreJni_deleteFriend(JNIEnv *env, jobject thiz, jstring friendid) {
    if (friendid == NULL)
        return -2;
    char *friendid_p = Jstring2CStr(env, friendid);
    if (friendid_p == NULL) {
        return -3;
    }
    return tox_friend_delete(mTox, friendid_p, NULL);
}

JNIEXPORT void Java_com_stratagile_toxcore_ToxCoreJni_setCallBack(JNIEnv *env, jobject thiz) {
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
JNIEXPORT jint JNICALL Java_com_stratagile_toxcore_ToxCoreJni_sendMessage(JNIEnv *env, jobject thiz, jstring message,
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
    LOGD("%s", message_p);
    LOGD("%d", tox_self_get_friend_list_size(mTox));
//    uint32_t *list;
//    tox_self_get_friend_list(mTox, list);
//    LOGD("第一个 %s", (char*)list[0]);
    if (friendNum < 0) {
        LOGD("friendNum= %d", friendNum);
        return -1;
    }
    TOX_ERR_FRIEND_SEND_MESSAGE error;
    int result =  tox_friend_send_message(mTox, friendNum, TOX_MESSAGE_TYPE_NORMAL, message_p, strlen(message_p), &error);
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

int get_friend_num_in_friendlist() {

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
    LOGD("好友上线");
    char fraddr_str[FRAPUKKEY_TOSTR_BUFSIZE];
    uint8_t fraddr_bin[TOX_PUBLIC_KEY_SIZE];
    if (tox_friend_get_public_key(tox, friend_number, fraddr_bin, NULL)) {
        frpuk_to_str(fraddr_bin, fraddr_str);
//        publickey = (*env)->NewStringUTF(env,fraddr_str);
    }
    LOGD("%s", (char *) fraddr_str);
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
    jmethodID mid_construct = NULL;
    jobject jobj = NULL;

    if((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL)  !=  JNI_OK) {
        return;
    }
    //直接用GetObjectClass找到Class, 也就是Sdk.class.
    jclass clazz = (*env)->FindClass(env, "com/stratagile/toxcore/ToxCoreJni");
    if (clazz == NULL) {
        LOGD("找不到'com/stratagile/toxcore/ToxCoreJni'这个类");
        return;
    }
    // 2、获取类的默认构造方法ID
    mid_construct = (*env)->GetMethodID(env, clazz, "<init>", "()V");
    if (mid_construct == NULL) {
        LOGD("找不到默认的构造方法");
        return;
    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env)->GetMethodID(env, clazz, "receivedMessage",
                                                 "(Ljava/lang/String;Ljava/lang/String;)V");
    //创建该类的实例
    jobj = (*env)->NewObject(env, clazz, mid_construct);
    if (jobj == NULL) {
        LOGD("在com/stratagile/toxcore/ToxCoreJni类中找不到receivedMessage方法");
        return;
    }
    LOGD("开始调用java方法");
    jstring callbackStr = (*env)->NewStringUTF(env, string);
    jstring jfriendNumber = (*env)->NewStringUTF(env, friendNumber);
    //进行回调，ret是java层的返回值（这个有些场景很好用）
    (*env)->CallVoidMethod(env, jobj, javaCallback, jfriendNumber, callbackStr);

    (*env)->DeleteLocalRef(env, clazz);
    (*env)->DeleteLocalRef(env, jobj);
    (*env)->DeleteLocalRef(env, callbackStr);
    (*env)->DeleteLocalRef(env, jfriendNumber);
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
int GetFriendNumInFriendlist(uint8_t *friendId_P)
{

    if(mTox != NULL)
    {
        if(friendId_P == NULL)
        {
            return -2;
        }
        char *friendId_bin = hex_string_to_bin(friendId_P);
        if(friendId_bin==NULL)
            return -3;
        int friendLoc = tox_friend_get_Num_in_friendlist(mTox, friendId_bin, NULL);
        //printf("This friend loc is %d\n", friendLoc);

        free(friendId_bin);
        if (friendLoc == -1)
        {
            return -4;
        }
        else
            return friendLoc;
    }
    return -1;

}

void deleteFriendAll() {
    int friendcounts = tox_self_get_friend_list_size(mTox);
    int i;
    for(i=0;i<friendcounts;i++)
    {
        int res = tox_friend_delete(mTox, friendcounts - 1 - i, NULL);
        if (res) {
            printf("remove a friend success\n");
            //save_data(qlinkNode);
        } else {
            printf("remove a friend fail\n");
        }
    }
}




