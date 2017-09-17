package com.mote.player360.program;

import android.content.Context;
import android.opengl.GLES30;

import com.mote.player360.util.ShaderHelper;

/**
 * Created by Administrator on 2017/8/22.
 */

public class PlaneRenderProgram extends RenderProgram{
    private int muSTMatrixHandle;
    private int uTextureSamplerHandle;

    public PlaneRenderProgram(Context context,int vertex_shader_id, int fragment_shader_id ){
        super(context,vertex_shader_id,fragment_shader_id);
    }

    @Override
    public void create(){
        super.create();
        muSTMatrixHandle = GLES30.glGetUniformLocation(getProgramId(), "uSTMatrix");
        ShaderHelper.checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        uTextureSamplerHandle= GLES30.glGetUniformLocation(getProgramId(),"sTexture");
        ShaderHelper.checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");
    }

    public int getMuSTMatrixHandle() {
        return muSTMatrixHandle;
    }

    public int getUTextureSamplerHandle() { return uTextureSamplerHandle; }

}
