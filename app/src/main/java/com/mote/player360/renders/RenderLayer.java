package com.mote.player360.renders;

import android.opengl.GLES30;

import java.util.LinkedList;

/**
 * Created by Wenstry on 2017/8/11.
 */

public abstract class RenderLayer {
    public static final String TAG = "RenderLayer";
    protected int mWidth;
    protected int mHeight;

    abstract public void onDrawFrame(final int textureId);

    abstract public void destroy();

    abstract public void init();

    public  RenderLayer(){
    }

    public void onRenderChanged(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void onPreDraw(){
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
    }

}
