package com.example.sooraj.getfit;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Fields
    private FirebaseDatabase database;
    private DatabaseReference users;
    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private String username;
    private User user;
    private boolean mServiceBound = false;
    private BoundService mBoundService;
    private ServiceConnection mServiceConnection;
    private boolean tabViewSetUpDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sooraj.getfit.R.layout.activity_main);

        //Gets reference to database
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        username = Objects.requireNonNull(getIntent().getExtras()).getString("username");
        tabViewSetUpDone = false;
        updateUser();


    }

    @Override
    protected void onStart() {
        super.onStart();
        startBoundService();
    }

    private void updateUser() {

        //Updates local reference to user every time user is updated in Firebase
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);


                //Ensures that tablayout is set up after initial reference to user is received
                //Ensures tablayout is only set up once
                //Sets the tab that is seen when the app is opened to the step counter tab
                if (!tabViewSetUpDone) {
                    setUpTabView();
                    viewPager.setCurrentItem(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public User getUser() {
        return user;
    }

    public void startBoundService() {

        //Starts bound service which counts steps and resets users steps and calories at midnight
        //Binds bound service to this activity
        Intent intent = new Intent(this, BoundService.class);
        intent.putExtra("username", username);
        startService(intent);

        //Setup a service connection which is connected to the bound service
        //Used to monitor the state of the service
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
                mBoundService = myBinder.getService();
                mServiceBound = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                startBoundService();
            }
        };

        //Binds bound service to this activity
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    public void setUpTabView() {

        //Gets reference to views
        //Sets up viewpager which allows user to scroll through tablayout
        toolbar = findViewById(com.example.sooraj.getfit.R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(com.example.sooraj.getfit.R.id.tabLayout);
        viewPager = findViewById(com.example.sooraj.getfit.R.id.viewPager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), username);
        viewPager.setAdapter(pageAdapter);


        //Changes toolbar text and color when a new tab is selected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_dark));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_dark));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_dark));
                        ProgressFragment.setToolbarText(toolbar);
                        break;

                    case 1:
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, com.example.sooraj.getfit.R.color.colorAccent));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, com.example.sooraj.getfit.R.color.colorAccent));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, com.example.sooraj.getfit.R.color.colorAccent));
                        toolbar.setTitle("View Activity");

                        break;

                    case 2:
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                        toolbar.setTitle("Track Diet");
                        break;

                    case 3:
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_dark));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_dark));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_dark));
                        toolbar.setTitle("Social");
                        break;

                    case 4:
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, com.example.sooraj.getfit.R.color.colorPrimary));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, com.example.sooraj.getfit.R.color.colorPrimary));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, com.example.sooraj.getfit.R.color.colorPrimary));
                        toolbar.setTitle("Your Profile");
                        break;

                    default:
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

        //Connects viewpager to tab layout
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabViewSetUpDone = true;

    }


}
