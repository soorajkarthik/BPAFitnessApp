package com.example.sooraj.fitnessapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GetInformationActivity extends AppCompatActivity {

    //firebase
    FirebaseDatabase database;
    DatabaseReference users;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getinformation);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        final String username = Objects.requireNonNull(getIntent().getExtras()).getString("Username");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
