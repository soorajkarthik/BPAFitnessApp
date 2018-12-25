package com.example.sooraj.fitnessapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabItem progressTab;
    private TabItem stepsTab;
    private TabItem foodTab;
    private TabItem profileTab;
    private TextView stepText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString((R.string.app_name)));
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        progressTab = findViewById(R.id.progressTab);
        stepsTab = findViewById(R.id.stepsTab);
        foodTab = findViewById(R.id.foodTab);
        profileTab = findViewById(R.id.profileTab);
        viewPager = findViewById(R.id.viewPager);

        stepText = findViewById(R.id.stepText);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                 switch (tab.getPosition()) {
                     case 1:
                         toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                         tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                         getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                         break;

                     case 2:
                         toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                         tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                         getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                         break;

                     case 3:
                         toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                         tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                         getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                         break;

                     default:
                         toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                         tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                         getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                         break;
                 }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    private int stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;
    private boolean isRunning = false;

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

    }

    @Override
    public void onStart() {
        super.onStart();
        if(isRunning)
            return;

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        isRunning = true;

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



}
