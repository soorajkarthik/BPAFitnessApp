package com.example.sooraj.fitnessapp;

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

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

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
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);

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
        Intent intent = new Intent(this, BoundService.class);
        intent.putExtra("username", username);
        startService(intent);

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

        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    public void setUpTabView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), username);
        viewPager.setAdapter(pageAdapter);


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
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                        toolbar.setTitle("View Activity");

                        break;

                    case 2:
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                        toolbar.setTitle("Track Diet");
                        break;

                    case 3:
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
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
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabViewSetUpDone = true;

    }


}
