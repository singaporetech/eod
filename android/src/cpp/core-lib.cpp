#include <jni.h>
#include <string>

// for logging in native
#include <android/log.h>
#define TAG "IN NATIVE"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_boliao_eod_Splash_getNativeString(JNIEnv *env, jobject thiz) {
    std::string secretStr = "THIS IS FROM C++";
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "created string = %s", secretStr.c_str());
    return env->NewStringUTF(secretStr.c_str());
}