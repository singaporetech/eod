//
// Created by Chek Tien Tan on 18/3/18.
//

#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {

static const char* TAG = "NATIVE-LIB";

JNIEXPORT jstring JNICALL
Java_com_boliao_eod_Splash_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    // debugging print to logcat
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "native str = %s", hello.c_str());

    return env->NewStringUTF(hello.c_str());
}

}
