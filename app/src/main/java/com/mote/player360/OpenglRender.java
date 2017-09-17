package com.mote.player360;

/**
 * Created by Wenstery on 2017/8/8.
 */

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.mote.player360.renders.PlaneRenderLayer;
import com.mote.player360.renders.RenderLayer;
import com.mote.player360.renders.SphereRenderLayer;
import com.mote.player360.renders.TransformRenderLayer;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenglRender implements GLSurfaceView.Renderer {
    private static final String TAG = "OpenglRender";
    private boolean orientationActive;
    private ViewPlayer viewPlayer;
    private PlaneRenderLayer planeRenderLayer;
    private SphereRenderLayer sphereRenderLayer;
    private TransformRenderLayer transformRenderLayer;
    private List<RenderLayer> renderLayerList;
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private int[] frameBuffers = null;
    private int[] frameBufferTextures = null;

    public OpenglRender(Context context) {
        mContext = context;
        init();
    }

    public void setViewPlayer(ViewPlayer player) {
        viewPlayer = player;
    }

    public void setOrientationActive(boolean active) {
        orientationActive = active;
    }

    public void init() {
        renderLayerList = new ArrayList<>();
        planeRenderLayer = new PlaneRenderLayer(mContext);
        renderLayerList.add(planeRenderLayer);
        sphereRenderLayer = new SphereRenderLayer(mContext, orientationActive);
        renderLayerList.add(sphereRenderLayer);
        transformRenderLayer = new TransformRenderLayer(mContext);
        renderLayerList.add(transformRenderLayer);
        onRenderLayerChanged(mWidth, mHeight);

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        for (int i = 0; i < renderLayerList.size(); i++) {
            renderLayerList.get(i).init();
        }
        viewPlayer.setSurface(planeRenderLayer.getPlaneRenderTexture().getTextureId());
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glFrontFace(GLES30.GL_CW);
        GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        viewPlayer.doTextureUpdate(planeRenderLayer.getSTMatrix());
        if (frameBuffers == null || frameBufferTextures == null) {
            return;
        }
        int size = renderLayerList.size();
        int previousTexture = planeRenderLayer.getPlaneRenderTexture().getTextureId();
        for (int i = 0; i < size; i++) {
            RenderLayer renderLayer = renderLayerList.get(i);
            if (i < size - 1) {
                GLES30.glViewport(0, 0, mWidth, mHeight);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffers[i]);
                GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                renderLayer.onDrawFrame(previousTexture);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                previousTexture = frameBufferTextures[i];
            } else {
                GLES30.glViewport(0, 0, mWidth, mHeight);
                renderLayer.onDrawFrame(previousTexture);
            }
        }
        GLES30.glDisable(GLES30.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
        GLES30.glViewport(0, 0, width, height);
        onRenderLayerChanged(width, height);
    }

    public void onRenderLayerChanged(int surfaceWidth, int surfaceHeight) {
        int size = renderLayerList.size();
        for (int i = 0; i < size; i++) {
            RenderLayer layer = renderLayerList.get(i);
            layer.onRenderChanged(surfaceWidth, surfaceHeight);
        }
        if (frameBuffers != null) {
            destroyFrameBuffers();
        }
        if (frameBuffers == null) {
            frameBuffers = new int[size - 1];
            frameBufferTextures = new int[size - 1];

            for (int i = 0; i < size - 1; i++) {
                GLES30.glGenFramebuffers(1, frameBuffers, i);
                GLES30.glGenTextures(1, frameBufferTextures, i);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, frameBufferTextures[i]);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, surfaceWidth, surfaceHeight, 0,
                        GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffers[i]);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                        GLES30.GL_TEXTURE_2D, frameBufferTextures[i], 0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            }
        }
    }

    private void destroyFrameBuffers() {
        if (frameBufferTextures != null) {
            GLES30.glDeleteTextures(frameBufferTextures.length, frameBufferTextures, 0);
            frameBufferTextures = null;
        }
        if (frameBuffers != null) {
            GLES30.glDeleteFramebuffers(frameBuffers.length, frameBuffers, 0);
            frameBuffers = null;
        }
    }

    public void destroy() {
        destroyFrameBuffers();
        for (int i = 0; i < renderLayerList.size(); i++) {
            renderLayerList.get(i).destroy();
        }
        if (getSphereRenderLayer() != null) {
            getSphereRenderLayer().getSensorProcessor().releaseResources();
        }
    }

    public SphereRenderLayer getSphereRenderLayer() {
        return sphereRenderLayer;
    }
}
