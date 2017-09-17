package com.mote.player360.model;

import android.opengl.GLES30;
import android.util.Log;

import com.mote.player360.util.ShaderHelper;

import java.nio.FloatBuffer;

/**
 * Created by Wenstry on 2017/8/22.
 */

public class Plane {
    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mTexCoordinateBuffer;
    private static final float TRIANGLES_DATA_CW[] = {
            -1.0f, -1.0f, 0f,
            -1.0f, 1.0f, 0f,
            1.0f, -1.0f, 0f,
            1.0f, 1.0f, 0f
    };

    public Plane(boolean isInGroup) {
        mVerticesBuffer = ShaderHelper.getFloatBuffer(TRIANGLES_DATA_CW, 0);
        if (isInGroup)
            mTexCoordinateBuffer = ShaderHelper.getFloatBuffer(PlaneTextureRotation.getRotation(PlaneTextureRotation.Rotation.NORMAL, false, true), 0);
        else
            mTexCoordinateBuffer = ShaderHelper.getFloatBuffer(PlaneTextureRotation.TEXTURE_NO_ROTATION, 0);
    }

    public void uploadVerticesBuffer(int positionHandle) {
        FloatBuffer vertexBuffer = getVerticesBuffer();
        if (vertexBuffer == null) return;
        vertexBuffer.position(0);

        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        ShaderHelper.checkGlError("glVertexAttribPointer mPosition");
        GLES30.glEnableVertexAttribArray(positionHandle);
        ShaderHelper.checkGlError("glEnableVertexAttribArray mPositionHandle");
    }

    public void uploadTexCoordinateBuffer(int textureCoordinateHandle) {
        FloatBuffer textureBuffer = getTexCoordinateBuffer();
        if (textureBuffer == null) return;
        textureBuffer.position(0);

        GLES30.glVertexAttribPointer(textureCoordinateHandle, 2, GLES30.GL_FLOAT, false, 0, textureBuffer);
        ShaderHelper.checkGlError("glVertexAttribPointer mTextureHandle");
        GLES30.glEnableVertexAttribArray(textureCoordinateHandle);
        ShaderHelper.checkGlError("glEnableVertexAttribArray mTextureHandle");
    }


    public FloatBuffer getVerticesBuffer() {
        return mVerticesBuffer;
    }

    public FloatBuffer getTexCoordinateBuffer() {
        return mTexCoordinateBuffer;
    }

    //only used to flip texture
    public void setTexCoordinateBuffer(FloatBuffer mTexCoordinateBuffer) {
        this.mTexCoordinateBuffer = mTexCoordinateBuffer;
    }

    public void setVerticesBuffer(FloatBuffer mVerticesBuffer) {
        this.mVerticesBuffer = mVerticesBuffer;
    }

    public void draw() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
    }

    public Plane scale(float scalingFactor) {
        float[] temp = new float[TRIANGLES_DATA_CW.length];
        System.arraycopy(TRIANGLES_DATA_CW, 0, temp, 0, TRIANGLES_DATA_CW.length);
        for (int i = 0; i < temp.length; i++) {
            temp[i] *= scalingFactor;
        }
        mVerticesBuffer = ShaderHelper.getFloatBuffer(temp, 0);
        return this;
    }
}
