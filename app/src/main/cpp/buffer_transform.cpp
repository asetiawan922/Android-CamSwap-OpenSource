#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <dlfcn.h>

typedef int32_t (*ANativeWindow_setBuffersTransform_fn)(ANativeWindow* window, int32_t transform);

// Forwards the camera's buffer transform to an EGL-produced output, so the app sees the
// same orientation metadata as when the camera writes to its Surface directly.
extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_zensu357_camswap_SwitchablePreview_nativeSetBuffersTransform(
        JNIEnv* env, jclass /*clazz*/, jobject surface, jint transform) {
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (window == nullptr) return JNI_FALSE;

    static auto pfnSetBuffersTransform = (ANativeWindow_setBuffersTransform_fn)
            dlsym(RTLD_DEFAULT, "ANativeWindow_setBuffersTransform");

    jboolean result = JNI_FALSE;
    if (pfnSetBuffersTransform != nullptr) {
        int32_t res = pfnSetBuffersTransform(window, transform);
        result = (res == 0) ? JNI_TRUE : JNI_FALSE;
    }
    ANativeWindow_release(window);
    return result;
}
