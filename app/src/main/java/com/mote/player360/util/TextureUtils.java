package com.mote.player360.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import com.mote.player360.textures.GLTextureConf;


public class TextureUtils {
    private static final String TAG = "TextureUtils";

    public static void bindTexture2D(int textureId, int activeTextureID, int handle, int idx) {
        if (textureId != GLTextureConf.NO_TEXTURE) {
            GLES30.glActiveTexture(activeTextureID);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(handle, idx);
        }
    }


    public static void bindTextureOES(int textureId, int activeTextureID, int handle, int idx) {
        if (textureId != GLTextureConf.NO_TEXTURE) {
            GLES30.glActiveTexture(activeTextureID);
            GLES30.glBindTexture(GLTextureConf.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES30.glUniform1i(handle, idx);
        }
    }

    public static int loadTextureFromResources(Context context, int resourceId, int imageSize[]) {
        return getTextureFromBitmap(
                BitmapUtils.loadBitmapFromRaw(context, resourceId),
                imageSize);
    }

    public static int loadTextureFromAssets(Context context, String filePath, int imageSize[]) {
        return getTextureFromBitmap(
                BitmapUtils.loadBitmapFromAssets(context, filePath),
                imageSize);
    }

    public static int getTextureFromBitmap(Bitmap bitmap, int imageSize[]) {
        final int[] textureObjectIds = new int[1];
        GLES30.glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            Log.d(TAG, "Failed at glGenTextures");
            return 0;
        }

        if (bitmap == null) {
            Log.d(TAG, "Failed at decoding bitmap");
            GLES30.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        if (imageSize != null && imageSize.length >= 2) {
            imageSize[0] = bitmap.getWidth();
            imageSize[1] = bitmap.getHeight();
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureObjectIds[0]);

        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        return textureObjectIds[0];
    }
}
