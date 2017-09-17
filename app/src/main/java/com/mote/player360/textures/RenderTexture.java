package com.mote.player360.textures;

import android.opengl.GLES30;

import com.mote.player360.util.ShaderHelper;


/**
 * Created by Wenstery on 2017/8/20.
 */

public class RenderTexture {
    public final static String TAG = "RenderTexture";
    private int textureId;

    private boolean textureLoaded;

    public RenderTexture() {
        textureId = GLTextureConf.NO_TEXTURE;
        textureLoaded = false;
    }

    public void loadTexture() {
        if (textureLoaded) {
            return;
        }
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        textureId = textures[0];
        GLES30.glBindTexture(GLTextureConf.GL_TEXTURE_EXTERNAL_OES, textureId);
        ShaderHelper.checkGlError("glBindTexture textureId");
        GLES30.glTexParameterf(GLTextureConf.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLTextureConf.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR);
        textureLoaded = true;
    }

    public void deleteTexture() {
        int[] textures = new int[1];
        textures[0] = textureId;
        GLES30.glDeleteTextures(1, textures, 0);
    }

    public int getTextureId() {
        return textureId;
    }
}
