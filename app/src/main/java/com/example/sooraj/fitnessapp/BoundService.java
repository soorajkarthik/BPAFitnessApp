package com.example.sooraj.fitnessapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class BoundService extends Service implements SensorEventListener {

    private IBinder mBinder = new MyBinder();
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        isRunning = true;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private int stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;

    @Override
    public void onSensorChanged (SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                stepDetector++;
                break;
            case Sensor.TYPE_STEP_COUNTER:
                if(counterSteps < 1) {
                    counterSteps = (int) sensorEvent.values[0];
                }
                stepCounter = (int) sensorEvent.values[0];
                break;

            default:
                break;
        }

        System.out.println(stepCounter);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
        isRunning = false;
    }

    public class MyBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }
}
