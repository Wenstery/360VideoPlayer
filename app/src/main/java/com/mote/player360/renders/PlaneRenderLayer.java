package com.mote.player360.renders;

import com.mote.player360.R;
import com.mote.player360.model.Plane;
import com.mote.player360.program.PlaneRenderProgram;
import com.mote.player360.textures.RenderTexture;
import com.mote.player360.util.TextureUtils;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

/**
 * Created by Wenstery on 2017/8/20.
 */

public class PlaneRenderLayer extends RenderLayer {
    private RenderTexture planeRenderTexture;
    private PlaneRenderProgram planeRenderProgram;
    private Plane plane;
    private int vertex_shader_id = R.raw.plane_vertex;
    private int fragment_shader_id = R.raw.plane_fragment;
    private float[] mSTMatrix = new float[16];

    public PlaneRenderLayer(Context context) {
        plane = new Plane(true);
        planeRenderProgram = new PlaneRenderProgram(context, vertex_shader_id, fragment_shader_id);
        planeRenderTexture = new RenderTexture();
        Matrix.setIdentityM(mSTMatrix, 0);
    }

    @Override
    public void onPreDraw() {
        super.onPreDraw();
        planeRenderProgram.use();
        plane.uploadTexCoordinateBuffer(planeRenderProgram.getTextureCoordinateHandle());
        plane.uploadVerticesBuffer(planeRenderProgram.getPositionHandle());
        GLES30.glUniformMatrix4fv(planeRenderProgram.getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);
    }

    @Override
    public void init() {
        planeRenderProgram.create();
        planeRenderTexture.loadTexture();
    }

    @Override
    public void destroy() {
        planeRenderProgram.onDestroy();
        planeRenderTexture.deleteTexture();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDraw();
        TextureUtils.bindTextureOES(textureId, GLES30.GL_TEXTURE0, planeRenderProgram.getUTextureSamplerHandle(), 0);
        GLES30.glViewport(0, 0, mWidth, mHeight);
        plane.draw();
    }

    public PlaneRenderProgram getPlaneRenderProgram() {
        return planeRenderProgram;
    }

    public RenderTexture getPlaneRenderTexture() {
        return planeRenderTexture;
    }

    public float[] getSTMatrix() {
        return mSTMatrix;
    }
}
