#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <android/log.h>
#include <android/bitmap.h>

using namespace cv;

#define TAG "UtilsNative"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_duzhaokun123_utils_MatsKt_nMatSurroundWith(JNIEnv*, jclass, jlong mat,
                                                          jint width, jint color) {
    auto r = color & 0xFF0000;
    auto g = color & 0x00FF00;
    auto b = color & 0x0000FF;
    auto i = (Mat*)mat;
    auto o = new Mat(i->rows + width * 2, i->cols + width * 2, CV_8UC3, Scalar(b, g, r));
    typedef cv::Point3_<uint8_t> Pixel;
    i->forEach<Pixel>([&](Pixel& pixel, const int position[]) -> void {
        o->at<Pixel>(position[0] + width, position[1] + width) = pixel;
    });
    return reinterpret_cast<jlong>(o);
}
