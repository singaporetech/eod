#include <jni.h>
#include <string>

// for logging in native
#include <android/log.h>
#define TAG "IN NATIVE"

// TODO NDK 3: include opencv headers
#include <opencv2/opencv.hpp>
using namespace cv;

extern "C" {

//static const char* TAG = "NATIVE-LIB";

/**
 * Convert rgba values to grayscale.
 * @param rgbaInput
 * @param rgbaOutput
 * @return
 */
int convertRGBA2Gray(Mat& rgbaInput, Mat& rgbaOutput) {
    cvtColor(rgbaInput, rgbaOutput, COLOR_RGBA2GRAY);

    // determine if success
    if (rgbaOutput.rows == rgbaInput.rows && rgbaOutput.cols == rgbaInput.cols)
        return 1;
    return 0;
}

/**
 * Draw bounding regions around detected objects.
 * @param cascadePath
 * @param rgbaInput passed by ref and regions will be drawn onto it
 */
void detectFace(const std::string& cascadePath, Mat& rgbaInput) {
    // Load Face cascade (.xml file)
    CascadeClassifier face_cascade;

    // load file
    if (!face_cascade.load(cascadePath))
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "face cascade loading error");

    // to collect regions detected
    std::vector<Rect> regions;

    // convert input to grayscale
    Mat rgbaOutput;
    cvtColor(rgbaInput, rgbaOutput, COLOR_RGBA2GRAY);

    // detect the face and store in faces
    face_cascade.detectMultiScale( rgbaOutput,
                                   regions,
                                   1.35,
                                   1,
                                   CASCADE_FIND_BIGGEST_OBJECT|CASCADE_SCALE_IMAGE,
                                   Size(30, 30));

    // Draw circles on the detected faces
    for( size_t i = 0; i < regions.size(); ++i ) {
        Point2d center( regions[i].x + regions[i].width*0.7, regions[i].y + regions[i].height*0.7 );
        ellipse( rgbaInput, center, Size2d( regions[i].width*0.5, regions[i].height*0.5), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );
    }

    // flip the pixel orientation to display correctly
    flip(rgbaInput, rgbaInput, 0);
}

/**
 * JNI func to convert image to grayscale.
 * @param env
 * @param rgbaAddrInput
 * @param rgbaAddrOutput
 * @return
 */
JNIEXPORT jint JNICALL
Java_com_boliao_eod_Splash_convertToGrayscale(
        JNIEnv *env,
        jobject /* this */,
        jlong rgbaAddrInput, jlong rgbaAddrOutput) {

    // cast the address to Mat* and get the data pointed by it, hold a ref to it
    Mat& rgbaInput = *(Mat*)rgbaAddrInput;
    Mat& rgbaOutput = *(Mat*)rgbaAddrOutput;

    // debugging print to logcat
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "output addr=%ld", rgbaAddrOutput);

    // convert to grayscale
    return convertRGBA2Gray(rgbaInput, rgbaOutput);
}

/**
 * JNI func to detect objects on screen.
 * @param env
 * @param cascadePath
 * @param rgbaAddrInput
 */
JNIEXPORT void JNICALL
Java_com_boliao_eod_Splash_detectFace(
        JNIEnv *env,
        jobject /* this */,
        jstring cascadePath,
        jlong rgbaAddrInput) {

    // convert jstring to c++ std string
    std::string cascadePathStr(env->GetStringUTFChars(cascadePath, NULL));

    // cast the address to Mat* and get the data pointed by it, hold a ref to it
    Mat& rgbaInput = *(Mat*)rgbaAddrInput;

    detectFace(cascadePathStr, rgbaInput);
}

JNIEXPORT jstring JNICALL
Java_com_boliao_eod_Splash_getNativeString(JNIEnv *env, jobject thiz) {
    std::string secretStr = "THIS IS FROM C++";
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "created string = %s", secretStr.c_str());
    return env->NewStringUTF(secretStr.c_str());
}

}