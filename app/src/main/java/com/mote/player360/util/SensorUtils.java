package com.mote.player360.util;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.view.Surface;

/**
 * Created by Wenstry on 2017/9/10.
 */

public class SensorUtils {
    private static float[] mTmp = new float[16];
    private static float[] oTmp = new float[16];

    public static void sensorRotationVectorToMatrix(SensorEvent event, int deviceRotation, float[] output) {
        float[] values = event.values;
        switch (deviceRotation) {
            case Surface.ROTATION_0:
                SensorManager.getRotationMatrixFromVector(output, values);
                break;
            default:
                SensorManager.getRotationMatrixFromVector(mTmp, values);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, output);
        }
        Matrix.rotateM(output, 0, 90.0F, 1.0F, 0.0F, 0.0F);
    }

    public static void getOrientation(SensorEvent event, float[] output) {
        SensorManager.getRotationMatrixFromVector(oTmp, event.values);
        SensorManager.getOrientation(oTmp, output);
    }

    public static void getOrientationFromRotationMatrix(float[] rotationMatrix, float[] output) {
        SensorManager.getOrientation(rotationMatrix, output);
    }
}
