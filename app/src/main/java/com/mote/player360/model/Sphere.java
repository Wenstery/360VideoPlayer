package com.mote.player360.model;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.mote.player360.util.ShaderHelper.checkGlError;

/**
 * Created by Wenstery on 2017/8/24.
 */

public class Sphere {
    private static final int sPositionDataSize = 3;
    private static final int sTextureCoordinateDataSize = 2;

    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mTexCoordinateBuffer;
    private ShortBuffer indexBuffer;
    private int mNumIndices;

    /**
     * original source code:
     * https://github.com/shulja/viredero/blob/a7d28b21d762e8479dc10cde1aa88054497ff649/viredroid/src/main/java/org/viredero/viredroid/Sphere.java
     *
     * @param radius  半径，半径应该在远平面和近平面之间
     * @param rings
     * @param sectors
     */
    public Sphere(float radius, int rings, int sectors) {
        final float PI = (float) Math.PI;
        final float PI_2 = (float) (Math.PI / 2);

        float R = 1f / (float) rings;
        float S = 1f / (float) sectors;
        short r, s;
        float x, y, z;

        int numPoint = (rings + 1) * (sectors + 1);
        float[] vertexs = new float[numPoint * 3];
        float[] texcoords = new float[numPoint * 2];
        short[] indices = new short[numPoint * 6];

        //纹理映射
        int t = 0, v = 0;
        for (r = 0; r < rings + 1; r++) {
            for (s = 0; s < sectors + 1; s++) {
                x = (float) (Math.cos(2 * PI * s * S) * Math.sin(PI * r * R));
                y = (float) Math.sin(-PI_2 + PI * r * R);
                z = (float) (Math.sin(2 * PI * s * S) * Math.sin(PI * r * R));

                texcoords[t++] = s * S;
                texcoords[t++] = r * R;

                vertexs[v++] = x * radius;
                vertexs[v++] = y * radius;
                vertexs[v++] = z * radius;
            }
        }

        //球体绘制坐标索引，用于  glDrawElements
        int counter = 0;
        int sectorsPlusOne = sectors + 1;
        for (r = 0; r < rings; r++) {
            for (s = 0; s < sectors; s++) {
                indices[counter++] = (short) (r * sectorsPlusOne + s);       //(a)
                indices[counter++] = (short) ((r + 1) * sectorsPlusOne + (s));    //(b)
                indices[counter++] = (short) ((r) * sectorsPlusOne + (s + 1));  // (c)
                indices[counter++] = (short) ((r) * sectorsPlusOne + (s + 1));  // (c)
                indices[counter++] = (short) ((r + 1) * sectorsPlusOne + (s));    //(b)
                indices[counter++] = (short) ((r + 1) * sectorsPlusOne + (s + 1));  // (d)
            }
        }

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                vertexs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexs);
        vertexBuffer.position(0);
        ByteBuffer cc = ByteBuffer.allocateDirect(
                texcoords.length * 4);
        cc.order(ByteOrder.nativeOrder());
        FloatBuffer texBuffer = cc.asFloatBuffer();
        texBuffer.put(texcoords);
        texBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        mTexCoordinateBuffer = texBuffer;
        mVerticesBuffer = vertexBuffer;
        mNumIndices = indices.length;
    }


    public void uploadVerticesBuffer(int positionHandle) {
        FloatBuffer vertexBuffer = getVerticesBuffer();
        if (vertexBuffer == null) return;
        vertexBuffer.position(0);

        GLES30.glVertexAttribPointer(positionHandle, sPositionDataSize, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        checkGlError("glVertexAttribPointer mPosition");
        GLES30.glEnableVertexAttribArray(positionHandle);
        checkGlError("glEnableVertexAttribArray mPositionHandle");
    }

    public void uploadTexCoordinateBuffer(int textureCoordinateHandle) {
        FloatBuffer textureBuffer = getTexCoordinateBuffer();
        if (textureBuffer == null) return;
        textureBuffer.position(0);

        GLES30.glVertexAttribPointer(textureCoordinateHandle, sTextureCoordinateDataSize, GLES30.GL_FLOAT, false, 0, textureBuffer);
        checkGlError("glVertexAttribPointer mTextureHandle");
        GLES30.glEnableVertexAttribArray(textureCoordinateHandle);
        checkGlError("glEnableVertexAttribArray mTextureHandle");
    }


    public FloatBuffer getVerticesBuffer() {
        return mVerticesBuffer;
    }

    public FloatBuffer getTexCoordinateBuffer() {
        return mTexCoordinateBuffer;
    }

    public void draw() {
        if (indexBuffer != null) {
            indexBuffer.position(0);
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, mNumIndices, GLES30.GL_UNSIGNED_SHORT, indexBuffer);
        } else {
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mNumIndices);
        }
    }
}
