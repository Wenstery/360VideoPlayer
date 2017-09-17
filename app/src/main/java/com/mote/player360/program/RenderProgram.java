package com.mote.player360.program;

import android.content.Context;
import android.opengl.GLES30;

import com.mote.player360.util.ShaderHelper;
import com.mote.player360.util.TextResourceReader;

/**
 * Created by Administrator on 2017/8/20.
 */

public class RenderProgram {
    private int mProgramId;
    private String mVertexShader;
    private String mFragmentShader;
    private int maPositionHandle;
    private int maTextureCoordinateHandle;

    public int getProgramId(){
        return mProgramId;
    }

    public RenderProgram(Context context ,int vertex_shader_id, int fragment_shader_id){
        mVertexShader = TextResourceReader.readTextFileFromResource(context, vertex_shader_id);
        mFragmentShader=TextResourceReader.readTextFileFromResource(context, fragment_shader_id);
    }

    public void create(){
        mProgramId = ShaderHelper.linkProgram(mVertexShader, mFragmentShader);
        if (mProgramId == 0) {
            return;
        }

        maPositionHandle = GLES30.glGetAttribLocation(getProgramId(), "aPosition");
        ShaderHelper.checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureCoordinateHandle = GLES30.glGetAttribLocation(getProgramId(), "aTextureCoord");
        ShaderHelper.checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureCoordinateHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
    }

    public void use(){
        GLES30.glUseProgram(getProgramId());
        ShaderHelper.checkGlError("glUseProgram");
    }

    public void onDestroy(){
        GLES30.glDeleteProgram(mProgramId);
    }

    public int getPositionHandle() {
        return maPositionHandle;
    }

    public int getTextureCoordinateHandle() {
        return maTextureCoordinateHandle;
    }

}
