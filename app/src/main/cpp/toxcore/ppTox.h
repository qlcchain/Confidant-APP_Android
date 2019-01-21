//
// Created by 胡智鹏 on 2019/1/16.
//

#ifndef TOXCORENEW_PPTOX_H
#define TOXCORENEW_PPTOX_H

#include "tox.h"
#include "../../../../../../../Library/Android/sdk/ndk-bundle/sysroot/usr/include/jni.h"    //hzp
//#include "E:/Android-SDK/ndk-bundle/sysroot/usr/include/jni.h"                                //zl

#endif //TOXCORENEW_PPTOX_H

char*   Jstring2CStr(JNIEnv*   env,   jstring   jstr);
int save_data(Tox *tox);
int save_data_file(Tox *m, const char *path);
static Tox *load_data(void);
void fraddr_to_str(uint8_t *id_bin, char *id_str);
void print_formatted_message(Tox *m, char *message, uint32_t friendnum, uint8_t outgoing);
void received_message(JNIEnv *env, char *string, char *friendNumber);
void frpuk_to_str(uint8_t *id_bin, char *id_str);
void show_log(JNIEnv *env, char *string);
void print_tox_id(JNIEnv *env, Tox *tox);
void deleteFriendAll();
void friend_status_callback(JNIEnv *env, int status, char *friendNumber);
void call_java_sendfile_rate(int position, int filesize);
void call_java_receivedfile_rate(int friendNumber, int position, int filesize);
void call_java_start_receive_file(int freindNumber, int fileNumber, char *fileName);
void call_java_start_send_file(int friendNumber, int fileNumber);



