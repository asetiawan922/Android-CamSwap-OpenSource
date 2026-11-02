package io.github.zensu357.camswap;

import android.view.Surface;
import io.github.zensu357.camswap.utils.VideoManager;

public class SwitchablePreview {
    static {
        try {
            System.loadLibrary("camswap-native-hook");
        } catch (Throwable ignored) {
        }
    }

    private final Surface cameraSurface;
    private final Surface mediaSurface;

    public SwitchablePreview(Surface cameraSurface, Surface mediaSurface) {
        this.cameraSurface = cameraSurface;
        this.mediaSurface = mediaSurface;
    }

    public static boolean useOriginalCamera() {
        try {
            ConfigManager config = VideoManager.getConfig();
            if (config != null) {
                return config.getBoolean(ConfigManager.KEY_USE_ORIGINAL_CAMERA, false);
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public Surface getCameraSurface() {
        return cameraSurface;
    }

    public Surface getMediaSurface() {
        return mediaSurface;
    }

    public static SwitchablePreview create(Surface output) {
        if (output == null || !output.isValid()) {
            return null;
        }
        return new SwitchablePreview(output, output);
    }

    public static native boolean nativeSetBuffersTransform(Surface surface, int transform);
}

