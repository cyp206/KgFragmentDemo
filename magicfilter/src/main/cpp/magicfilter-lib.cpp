#include <jni.h>
#include <GLES3/gl3.h>
extern "C" void
Java_com_seu_magicfilter_jni_GLESNative_glReadPixels(JNIEnv *env, jclass type_, jint width,
jint height, jint format, jint type,
        jint offset) {
    glReadPixels(0, 0, width, height, format, type, NULL);
}