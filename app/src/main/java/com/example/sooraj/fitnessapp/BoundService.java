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
import android.support.annotation.NonNull;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;


public class BoundService extends Service implements SensorEventListener {

    private FirebaseDatabase database;
    private DatabaseReference users;
    private IBinder mBinder = new MyBinder();
    private boolean isRunning = false;
    private int stepCounter = 0;
    private int counterSteps = 0;
    private int startingSteps = 0;
    private int stepDetector = 0;
    private User user;
    private String username;

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        isRunning = true;
    }


    @Override
    public IBinder onBind(Intent intent) {
        username = intent.getExtras().getString("username");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);
                startingSteps = user.getSteps();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return mBinder;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(user != null) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_STEP_DETECTOR:
                    stepDetector++;
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    if (counterSteps < 1) {
                        counterSteps = (int) sensorEvent.values[0];
                    }
                    stepCounter = (int) sensorEvent.values[0] + startingSteps;
                    user.setSteps(stepCounter);
                    users.child(username).child("steps").setValue(user.getSteps());
                    break;

                default:
                    break;
            }


            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            if (minute == 59) {
                if (c.get(Calendar.HOUR_OF_DAY) == 23) {

                    Date date = c.getTime();
                    String dateString = date.toString();
                    user.putStepsStorage(dateString, stepCounter);
                    user.putCalorieStorage(dateString, user.getCalories());
                    stepCounter = 0;
                    startingSteps = 0;
                    user.setSteps(0);
                    user.resetFood();
                    users.child(username).setValue(user);
                }
            }
        }
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
