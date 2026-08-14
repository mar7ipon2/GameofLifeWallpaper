#include <jni.h>
#include <vector>
#include <cstdint>

// Include the header file instead of the .cpp implementation
#include "GameOfLife.h"

static GameOfLife* g_game = nullptr;
static int g_width = 0;
static int g_height = 0;

extern "C" {
    // Initializes or resets the native GameOfLife engine instance.
JNIEXPORT void JNICALL
Java_com_example_gameoflifewallpaper_NativeEngine_initGame(
        JNIEnv* env,
        jobject /* this */,
        jint width,
        jint height,
        jint chunkSize,
        jint patternIndex) {

    g_width = width;
    g_height = height;

    if (g_game != nullptr) {
        delete g_game;
    }

    g_game = new GameOfLife(width, height, chunkSize, patternIndex);
}

// Advances the simulation by one generation.
JNIEXPORT void JNICALL
Java_com_example_gameoflifewallpaper_NativeEngine_updateGame(
        JNIEnv* env,
        jobject /* this */) {

    if (g_game != nullptr) {
        g_game->update();
    }
}

// Copies native grid data into a Java byte array for UI rendering.
JNIEXPORT jbyteArray JNICALL
Java_com_example_gameoflifewallpaper_NativeEngine_getGrid(
        JNIEnv* env,
        jobject /* this */) {

    if (g_game == nullptr) {
        return nullptr;
    }

    const std::vector<uint8_t>& gridData = g_game->getGridData();
    jsize size = static_cast<jsize>(gridData.size());

    jbyteArray result = env->NewByteArray(size);
    if (result == nullptr) {
        return nullptr;
    }

    // Direct memory copy from C++ vector to JVM byte array
    env->SetByteArrayRegion(result, 0, size, reinterpret_cast<const jbyte*>(gridData.data()));

    return result;
}

// Toggles a specific cell state in the native grid.
JNIEXPORT void JNICALL
Java_com_example_gameoflifewallpaper_NativeEngine_setCell(JNIEnv *env, jobject thiz, jint x, jint y) {
    if (g_game != nullptr) {
        g_game->setCell(x, y); // Invoke the C++ engine method
    }
}

// Returns the current grid width.
JNIEXPORT jint JNICALL
Java_com_example_gameoflifewallpaper_NativeEngine_getWidth(JNIEnv *env, jobject thiz) {
    return g_game ? g_game->getWidth() : 0;
}

// Returns the current grid height.
JNIEXPORT jint JNICALL
Java_com_example_gameoflifewallpaper_NativeEngine_getHeight(JNIEnv *env, jobject thiz) {
    return g_game ? g_game->getHeight() : 0;
}

} // extern "C"