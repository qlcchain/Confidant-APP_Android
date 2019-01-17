//
// Created by 胡智鹏 on 2019/1/16.
//

#ifndef TOXCORENEW_PPTOX_H
#define TOXCORENEW_PPTOX_H

#include "tox.h"
#include "E:/Android-SDK/ndk-bundle/sysroot/usr/include/jni.h"

#endif //TOXCORENEW_PPTOX_H

char *Jstring2CStr(JNIEnv *env, jstring jstr);

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


