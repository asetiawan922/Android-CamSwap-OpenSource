package io.github.zensu357.camswap.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

public class ImageUtils {

    // Origin: https://blog.csdn.net/jacke121/article/details/73888732
    public static Bitmap getBMP(String file) throws Throwable {
        return BitmapFactory.decodeFile(file);
    }

    public static Bitmap rotateBitmap(Bitmap source, int degrees) {
        if (source == null || (degrees % 360) == 0) {
            return source;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap rotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        if (rotated != source) {
            source.recycle();
        }
        return rotated;
    }

    public static Bitmap fitBitmapToTargetAspect(Bitmap source, int targetWidth, int targetHeight) {
        if (source == null) {
            return null;
        }
        if (targetWidth <= 0 || targetHeight <= 0) {
            return source;
        }

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        if (sourceWidth <= 0 || sourceHeight <= 0) {
            return source;
        }

        float sourceAspect = (float) sourceWidth / (float) sourceHeight;
        float targetAspect = (float) targetWidth / (float) targetHeight;

        Rect cropRect;
        if (Math.abs(sourceAspect - targetAspect) < 0.001f) {
            cropRect = new Rect(0, 0, sourceWidth, sourceHeight);
        } else if (sourceAspect > targetAspect) {
            int croppedWidth = Math.max(1, Math.round(sourceHeight * targetAspect));
            int left = Math.max(0, (sourceWidth - croppedWidth) / 2);
            cropRect = new Rect(left, 0, Math.min(sourceWidth, left + croppedWidth), sourceHeight);
        } else {
            int croppedHeight = Math.max(1, Math.round(sourceWidth / targetAspect));
            int top = Math.max(0, (sourceHeight - croppedHeight) / 2);
            cropRect = new Rect(0, top, sourceWidth, Math.min(sourceHeight, top + croppedHeight));
        }

        Bitmap cropped = source;
        if (cropRect.left != 0 || cropRect.top != 0 || cropRect.width() != sourceWidth
                || cropRect.height() != sourceHeight) {
            cropped = Bitmap.createBitmap(source, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());
            source.recycle();
        }

        if (cropped.getWidth() == targetWidth && cropped.getHeight() == targetHeight) {
            return cropped;
        }

        Bitmap scaled = Bitmap.createScaledBitmap(cropped, targetWidth, targetHeight, true);
        if (scaled != cropped) {
            cropped.recycle();
        }
        return scaled;
    }

    public static byte[] rgb2YCbCr420(int[] pixels, int width, int height) {
        int len = width * height;
        // yuv格式数组大小，y亮度占len长度，u,v各占len/4长度。
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = (pixels[i * width + j]) & 0x00FFFFFF;
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;
                // 套用公式
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                y = y < 16 ? 16 : (Math.min(y, 255));
                u = u < 0 ? 0 : (Math.min(u, 255));
                v = v < 0 ? 0 : (Math.min(v, 255));
                // 赋值
                yuv[i * width + j] = (byte) y;
                yuv[len + (i >> 1) * width + (j & ~1)] = (byte) u;
                yuv[len + +(i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

    public static byte[] getYUVByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return rgb2YCbCr420(pixels, width, height);
    }
}
