//
// Created by Chek Tien Tan on 17/3/19.
//

#include <jni.h>
#include <string>

// for logging in native
#include <android/log.h>
#define TAG "IN NATIVE"

extern "C" JNIEXPORT jstring JNICALL
Java_com_boliao_eod_Splash_getNativeString(JNIEnv* env, jobject /* this */) {
    std::string msg = "THIS IS FROM C++";
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "msg = %s", msg.c_str());
    return env->NewStringUTF(msg.c_str()); // note sometimes autocomplete will lag for C++
}