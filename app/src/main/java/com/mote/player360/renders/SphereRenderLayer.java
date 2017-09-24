package com.mote.player360.renders;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.mote.player360.R;
import com.mote.player360.model.Sphere;
import com.mote.player360.orientation.OrientationProcessor;
import com.mote.player360.program.TransformRenderProgram;
import com.mote.player360.sensor.SensorProcessor;
import com.mote.player360.util.TextureUtils;

/**
 * Created by Wenstry on 2017/8/20.
 */

public class SphereRenderLayer extends RenderLayer {
    private Sphere sphere;
    TransformRenderProgram glSphereProgram;
    private SensorProcessor sensorProcessor;
    private float[] rotationMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] modelViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float ratio;

    private float mDeltaX;
    private float mDeltaY;
    private float mScale;

    private int vertex_shader_id = R.raw.transform_vertex;
    private int fragment_shader_id = R.raw.transform_fragment;
    private boolean active = false;

    private OrientationProcessor orientationProcessor;

    public SphereRenderLayer(Context context, boolean active) {
        this.active = active;
        mDeltaX = -90;
        mDeltaY = 0;
        mScale = 1;
        sphere = new Sphere(18, 75, 150);
        sensorProcessor = new SensorProcessor();
        sensorProcessor.setSensorProcessorCallback(new SensorProcessor.SensorProcessorCallback() {
            @Override
            public void updateSensorMatrix(float[] sensorMatrix) {
                System.arraycopy(sensorMatrix, 0, rotationMatrix, 0, 16);
            }
        });
        sensorProcessor.init(context);
        glSphereProgram = new TransformRenderProgram(context, vertex_shader_id, fragment_shader_id);
        initMatrix();
        orientationProcessor = new OrientationProcessor();
    }

    @Override
    public void init() {
        glSphereProgram.create();
    }

    @Override
    public void onPreDraw() {
        super.onPreDraw();
    }

    @Override
    public void destroy() {
        glSphereProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        glSphereProgram.use();
        sphere.uploadTexCoordinateBuffer(glSphereProgram.getTextureCoordinateHandle());
        sphere.uploadVerticesBuffer(glSphereProgram.getPositionHandle());

        float currentDegree = (float) (Math.toDegrees(Math.atan(mScale)) * 2);
        Matrix.perspectiveM(projectionMatrix, 0, currentDegree, ratio, 1f, 500f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        if (active == true) {
            orientationProcessor.recordRotation(rotationMatrix);
            System.arraycopy(rotationMatrix, 0, modelMatrix, 0, 16);
            orientationProcessor.revertRotation(modelMatrix);
        }
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        GLES30.glUniformMatrix4fv(glSphereProgram.getMVPMatrixHandle(), 1, false, mMVPMatrix, 0);

        TextureUtils.bindTexture2D(textureId, GLES30.GL_TEXTURE0, glSphereProgram.getTextureSamplerHandle(), 0);

        onPreDraw();
        GLES30.glViewport(0, 0, mWidth, mHeight);
        sphere.draw();
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    }

    @Override
    public void onRenderChanged(int width, int height) {
        super.onRenderChanged(width, height);
        ratio = (float) width / height;
    }

    private void initMatrix() {
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 1.0f, 0.0f);
    }

    public SensorProcessor getSensorProcessor() {
        return sensorProcessor;
    }

    public float getDeltaX() {
        return mDeltaX;
    }

    public void setDeltaX(float mDeltaX) {
        this.mDeltaX = mDeltaX;
    }

    public float getDeltaY() {
        return mDeltaY;
    }

    public void setDeltaY(float mDeltaY) {
        this.mDeltaY = mDeltaY;
    }

    public void updateScale(float scaleFactor) {
        mScale = mScale + (1.0f - scaleFactor);
        mScale = Math.max(0.122f, Math.min(1.0f, mScale));
    }

}
