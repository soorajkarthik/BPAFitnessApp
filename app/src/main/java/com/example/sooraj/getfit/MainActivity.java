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

    /**
     * Fields
     */
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

    /**
     * Get reference to all components of the activity's view
     * Get reference to Firebase Database, and the "Users" node
     * @param savedInstanceState the last saved state of the application
     */
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

    /**
     * Starts BoundService once activity starts
     */
    @Override
    protected void onStart() {
        super.onStart();
        startBoundService();
    }

    /**
     * Called every time activity is revisited/started
     */
    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            updateLastSeen();
        }
    }

    /**
     * Updates the user's last seen time in Firebase
     */
    private void updateLastSeen() {
        user.setLastSeen(System.currentTimeMillis());
        users.child(username).child("lastSeen").setValue(user.getLastSeen());
    }

    /**
     * Updates local user reference to match Firebase
     * Sets up TabView the first time method is called
     */
    private void updateUser() {

        //Ensures local reference to user is updated every time user is updated in Firebase
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);


                /*
                 * Ensures that tablayout is set up after initial reference to user is received
                 * Ensures tablayout is only set up once
                 * Sets the tab that is seen when the app is opened to the step counter tab
                 */

                if (!tabViewSetUpDone) {
                    setUpTabView();
                    viewPager.setCurrentItem(1);
                    updateLastSeen();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /**
     * Public so method can be accessed by fragments
     * @return current user
     */
    public User getUser() {
        return user;
    }

    /**
     * Starts a BoundService which counts steps and resets users steps and calories at midnight
     * Binds BoundService to this activity
     */
    public void startBoundService() {

        Intent intent = new Intent(this, BoundService.class);
        intent.putExtra("username", username);
        startService(intent);

        //Setup a service connection which is connected to the bound service
        //Used to monitor the state of the service
        mServiceConnection = new ServiceConnection() {

            /**
             * Gets reference to BoundService once service has been connected to this activity
             */
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
                mBoundService = myBinder.getService();
                mServiceBound = true;
            }

            /**
             * Restarts BoundService if it stops for some reason
             * Ensures BoundService is always running
             */
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                startBoundService();
            }
        };

        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * Sets up toolbar
     * Set up view pager which allows user to scroll through TabLayout
     */
    public void setUpTabView() {


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            /**
             * Changes color and text of Toolbar and TabLayout based on the selected tab
             * @param tab the selected tab
             */
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
                        toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_dark));
                        tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_dark));
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_dark));
                        toolbar.setTitle("Social");
                        break;

                    case 4:
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
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        //Connects ViewPager to TabLayout
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabViewSetUpDone = true;
    }


}
