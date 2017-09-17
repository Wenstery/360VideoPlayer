package com.mote.player360.renders;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.mote.player360.R;
import com.mote.player360.model.Plane;
import com.mote.player360.program.TransformRenderProgram;
import com.mote.player360.util.TextureUtils;

/**
 * Created by Wenstry on 2017/9/10.
 */

public class TransformRenderLayer extends RenderLayer {
    protected TransformRenderProgram transformRenderProgram;
    private Plane plane;
    private int vertex_shader_id = R.raw.transform_vertex;
    private int fragment_shader_id = R.raw.transform_fragment;

    protected Context context;
    protected float[] projectionMatrix = new float[16];

    public TransformRenderLayer(Context context) {
        this.context = context;
        transformRenderProgram = new TransformRenderProgram(context,vertex_shader_id,fragment_shader_id);
        plane = new Plane(true);
    }

    @Override
    public void init() {
        transformRenderProgram.create();
    }

    @Override
    public void destroy() {
        transformRenderProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDraw();
        transformRenderProgram.use();
        Matrix.setIdentityM(projectionMatrix, 0);
        plane.uploadTexCoordinateBuffer(transformRenderProgram.getTextureCoordinateHandle());
        plane.uploadVerticesBuffer(transformRenderProgram.getPositionHandle());
        GLES20.glUniformMatrix4fv(transformRenderProgram.getMVPMatrixHandle(), 1, false, projectionMatrix, 0);
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0, transformRenderProgram.getTextureSamplerHandle(), 0);
        GLES20.glViewport(0, 0, mWidth, mHeight);
        plane.draw();
    }

    @Override
    public void onRenderChanged(int surfaceWidth, int surfaceHeight) {
        super.onRenderChanged(surfaceWidth, surfaceHeight);
    }

}
