package com.mote.player360.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.mote.player360.util.SensorUtils;

/**
 * Created by Wenstory on 2017/8/10.
 */

public class SensorProcessor implements SensorEventListener {
    public static String TAG = "SensorProcessor";
    private float[] rotationMatrix = new float[16];
    private SensorProcessorCallback sensorProcessorCallback;
    private boolean sensorRegistered;
    private SensorManager sensorManager;

    private int mDeviceRotation;

    private Context mContext;

    public void init(Context context) {
        mContext = context;
        sensorRegistered = false;
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorRot = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (sensorRot == null) return;
        sensorManager.registerListener(this, sensorRot, SensorManager.SENSOR_DELAY_GAME);
        sensorRegistered = true;
    }

    public void releaseResources() {
        if (!sensorRegistered || sensorManager == null) return;
        sensorManager.unregisterListener(this);
        sensorRegistered = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setSensorProcessorCallback(SensorProcessorCallback sensorProcessorCallback) {
        this.sensorProcessorCallback = sensorProcessorCallback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy != 0) {
            int type = event.sensor.getType();
            switch (type) {
                case Sensor.TYPE_ROTATION_VECTOR:
                    mDeviceRotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
                    SensorUtils.sensorRotationVectorToMatrix(event, mDeviceRotation, rotationMatrix);
                    sensorProcessorCallback.updateSensorMatrix(rotationMatrix);
                    break;
            }
        }
    }

    public interface SensorProcessorCallback {
        void updateSensorMatrix(float[] sensorMatrix);
    }
}
