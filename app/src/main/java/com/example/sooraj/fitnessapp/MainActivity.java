package com.example.sooraj.fitnessapp;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabItem stepsTab;
    private TabItem progressTab;
    private TabItem foodTab;
    private TabItem profileTab;
    private String username;
    private User user;

    FirebaseDatabase database;
    DatabaseReference users;

    private boolean mServiceBound = false;
    private BoundService mBoundService;

    public User getUser() {
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        username = Objects.requireNonNull(getIntent().getExtras()).getString("Username");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString((R.string.app_name)));
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        progressTab = findViewById(R.id.progressTab);

        stepsTab = findViewById(R.id.stepsTab);
        foodTab = findViewById(R.id.foodTab);
        profileTab = findViewById(R.id.profileTab);
        viewPager = findViewById(R.id.viewPager);


        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), username);
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
                        setStepTabText();
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


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BoundService.class);
        intent.putExtra("Username", username);
        startService(intent);

        ServiceConnection mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
                mBoundService = myBinder.getService();
                mServiceBound = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mServiceBound = false;

            }
        };

        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    public void setStepTabText() {

        user.setSteps(mBoundService.getStepCounter());

        Fragment stepsFragment = getSupportFragmentManager().findFragmentById(R.id.viewPager);
        TextView stepsText = findViewById(R.id.fragment_steps).findViewById(R.id.stepsText);
        stepsText.setText("" + user.getSteps());

        ProgressBar progressBar = findViewById(R.id.fragment_steps).findViewById(R.id.stepsProgressBar);
        int percent = (user.getSteps() * 100) / user.getStepGoal();
        progressBar.setProgress(percent);
        TextView percentCompleted = findViewById(R.id.fragment_steps).findViewById(R.id.percentOfStepGoalText);
        percentCompleted.setText(percent + "% of Goal");

        double milesWalked = (user.getSteps() * ((user.getHeight() * 0.413) / 12)) / 5280;
        TextView distanceWalked = findViewById(R.id.fragment_steps).findViewById(R.id.distanceWalkedText);
        DecimalFormat df = new DecimalFormat("0.00");
        String milesWalkedString = df.format(milesWalked);
        distanceWalked.setText(milesWalkedString + " Miles Walked");

        int caloriesBurned = (int) (0.4 * user.getWeight() * milesWalked);
        TextView caloriesBurnedText = findViewById(R.id.fragment_steps).findViewById(R.id.caloriesBurnedText);
        caloriesBurnedText.setText(caloriesBurned + " Calories Burned");
        user.setCaloriesBurned(caloriesBurned);

        users.child(username).child("steps").setValue(user.getSteps());
        users.child(username).child("caloriesBurned").setValue(user.getCaloriesBurned());
    }
}
