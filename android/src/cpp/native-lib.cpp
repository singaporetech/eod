//
// Created by Chek Tien Tan on 18/3/18.
//

#include <jni.h>
#include <string>
#include <android/log.h>

// TODO NDK
// include opencv headers
#include <opencv2/opencv.hpp>
using namespace cv;

extern "C" {

static const char* TAG = "NATIVE-LIB";

/**
 * TODO NDK
 * 1. try using the __android_log_print to debug
 * @param env
 * @return
 */
JNIEXPORT jstring JNICALL
Java_com_boliao_eod_Splash_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    // debugging print to logcat
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "native str = %s", hello.c_str());

    return env->NewStringUTF(hello.c_str());
}

/**
 * TODO NDK
 * 1. create face detection helper code
 * @param cascadePath
 * @param rgbaInput
 */
void detectFace(const std::string& cascadePath, Mat& rgbaInput) {
    // Load Face cascade (.xml file)
    // TODO change to file within project
    CascadeClassifier face_cascade; //( "/sdcard/opencv/haarcascade_eye_tree_eyeglasses.xml" );

    if (face_cascade.load(cascadePath))
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "face cascade loaded");
    else
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "face cascade loading error");

    // Detect faces
    std::vector<Rect> faces;

    // convert input to grayscale
//    cvtColor(rgbaInput, rgbaInput, CV_RGBA2GRAY);

    // detect the face and store in faces
    face_cascade.detectMultiScale( rgbaInput,
                                   faces,
                                   1.2,
                                   2,
                                   CV_HAAR_FIND_BIGGEST_OBJECT|CV_HAAR_SCALE_IMAGE,
                                   Size(30, 30));

    // Draw circles on the detected faces
    for( size_t i = 0; i < faces.size(); ++i ) {
        Point center( faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5 );
        ellipse( rgbaInput, center, Size( faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );
    }
}

/**
 * 1. create C method that calls opencv functions to do face detection.
 * @param env
 * @param cascadePath
 * @param rgbaAddr
 */
JNIEXPORT void JNICALL
Java_com_boliao_eod_Splash_detectFace(
        JNIEnv *env,
        jobject /* this */,
        jstring cascadePath,
        jlong rgbaAddr) {

    // convert jstring to c++ std string
    std::string cascadePathStr(env->GetStringUTFChars(cascadePath, NULL));

    // cast the address to Mat* and get the data pointed by it, hold a ref to it
    Mat& rgbaInput = *(Mat*)rgbaAddr;

    detectFace(cascadePathStr, rgbaInput);
}

}
