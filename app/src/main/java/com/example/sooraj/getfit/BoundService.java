package com.example.sooraj.getfit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BoundService extends Service implements SensorEventListener {

    /**
     * Fields
     */
    private static DatabaseReference users;
    private static User user;
    private static String username;
    private FirebaseDatabase database;
    private IBinder mBinder = new MyBinder();

    /**
     * Get reference to Firebase Database, and the "Users" node
     * Registers system step sensor so real-time step data can be accessed
     */
    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Gets reference to current user from Firebase
     * Starts alarm used to perform many updates at midnight everyday, see setAlarm method
     *
     * @param intent intent passed in when binding service to activity,
     *               it will also contain the username of the current user
     * @return custom binder class, see documentation for MyBinder class
     */
    @Override
    public IBinder onBind(Intent intent) {

        username = intent.getExtras().getString("username");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        setAlarm(getApplicationContext());
        return mBinder;
    }

    /**
     * Updates the user's step count if the sensor that triggered SensorEventListener
     *             was the system's step detector sensor
     * @param sensorEvent the event "heard" by the SensorEventListener interface
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (user != null) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                user.setSteps(user.getSteps() + 1);
                users.child(username).child("steps").setValue(user.getSteps());
            }
        }
    }

    /**
     * Required method in order to use SensorEventListener interface
     * No action necessary
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * Stops listening to the system's step detector sensor when BoundService is deleted
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    /**
     * Sets up alarm with broadcasts to a custom BroadcastReceiver at 11:59PM everyday
     * @param context application's current context
     */
    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MyAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    /**
     * Custom BroadcastReceiver which receives broadcasts from the alarm previously set up
     */
    public static class MyAlarm extends BroadcastReceiver {


        /**
         * Required empty constructor
         */
        public MyAlarm() {
            super();
        }

        /**
         *  Resets the users step count and calorie count and stores the user's steps, calorie count, and weight for the day
         *  Updates user in Firebase
         * @param context current application context
         * @param intent intent passed in by alarm
         */
        @Override
        public void onReceive(Context context, Intent intent) {


            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.child(username).getValue(User.class);
                    Calendar c = Calendar.getInstance();

                    Date date = new Date(System.currentTimeMillis() - 12 * 60 * 60 * 1000);
                    SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                    String dateString = df.format(date);

                    user.putStepsStorage(dateString, user.getSteps());
                    user.putCalorieStorage(dateString, user.getCalories());
                    user.putWeightStorage(dateString, user.getWeight());

                    user.setSteps(0);
                    user.resetFood();

                    users.child(username).setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    /**
     * Custom Binder class to simplify the process of getting the current service
     */
    public class MyBinder extends Binder {

        public BoundService getService() {
            return BoundService.this;
        }
    }
}
