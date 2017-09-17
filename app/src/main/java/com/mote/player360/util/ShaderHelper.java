package com.mote.player360.util;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2017/8/20.
 */

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";
    private static final boolean LoggerDebug = true;

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId == 0) {
            if (LoggerDebug) {
                Log.w(TAG, "Could not create new shader.");
            }
        }
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];

        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerDebug) {
            Log.v(TAG,
                    "Results of compiling source:" + "\n" + shaderCode + "\n:" + GLES20.glGetShaderInfoLog(
                            shaderObjectId));
        }

        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId);
            if (LoggerDebug) {
                Log.w(TAG, "Compilation of shader failed");
            }
        }
        return shaderObjectId;
    }

    public static int linkProgram(String vertexShader, String fragmentShader) {
        int vertexShaderId = ShaderHelper.compileVertexShader(vertexShader);
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShader);
        final int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            Log.w(TAG, "Could not create new program");
        }

        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);

        GLES20.glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (LoggerDebug) {
            Log.v(TAG, "Results of linking program:\n" + GLES20.glGetProgramInfoLog(programObjectId));
        }

        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programObjectId);
            if (LoggerDebug) {
                Log.w(TAG, "Linking of program failed.");
            }
        }

        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program:"
                + validateStatus[0]
                + "\nLog:"
                + GLES20.glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    public static void checkGlError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    public static FloatBuffer getFloatBuffer(final float[] array, int offset) {
        FloatBuffer bb = ByteBuffer.allocateDirect(
                array.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(array);
        bb.position(offset);
        return bb;
    }
}
