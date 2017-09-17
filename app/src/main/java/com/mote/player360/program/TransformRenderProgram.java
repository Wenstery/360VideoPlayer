package com.mote.player360.program;

import android.content.Context;
import android.opengl.GLES30;

import com.mote.player360.util.ShaderHelper;

/**
 * Created by Administrator on 2017/9/10.
 */

public class TransformRenderProgram extends RenderProgram{
    private int uMVPMatrixHandle;
    private int uTextureSamplerHandle;

    public TransformRenderProgram(Context context, int vertex_shader_id, int fragment_shader_id) {
        super(context, vertex_shader_id,fragment_shader_id);
    }

    @Override
    public void create() {
        super.create();
        uTextureSamplerHandle= GLES30.glGetUniformLocation(getProgramId(),"sTexture");
        ShaderHelper.checkGlError("glGetUniformLocation uniform sTexture");
        uMVPMatrixHandle=GLES30.glGetUniformLocation(getProgramId(),"uMVPMatrix");
        ShaderHelper.checkGlError("glGetUniformLocation uMVPMatrix");
    }


    public int getTextureSamplerHandle() {
        return uTextureSamplerHandle;
    }

    public int getMVPMatrixHandle() {
        return uMVPMatrixHandle;
    }
}
