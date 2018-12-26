package com.example.sooraj.fitnessapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GetInformationActivity extends AppCompatActivity  {

    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;
    User user;

    Button confirmButton;
    EditText editAge, editWeight, editStepGoal;
    Spinner gender, heightFeet, heightInches, weightGoal;


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

        confirmButton = (Button) findViewById(R.id.buttonConfirmProfile);
        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editWeight);
        editStepGoal = findViewById(R.id.editStepGoal);
        gender = findViewById(R.id.spinnerGender);
        heightFeet = findViewById(R.id.spinnerHeightFeet);
        heightInches = findViewById(R.id.spinnerHeightInches);
        weightGoal = findViewById(R.id.weightGoal);



        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editAge.getText().toString().equals("")) {
                    Toast.makeText(GetInformationActivity.this, "Please enter your Age", Toast.LENGTH_SHORT).show();
                }

                if(gender.getSelectedItem().toString().equals("Gender")) {
                    Toast.makeText(GetInformationActivity.this, "Please select your Gender", Toast.LENGTH_SHORT).show();
                }

                if(heightFeet.getSelectedItem().toString().equals("Feet") || heightInches.getSelectedItem().toString().equals("Inches")) {
                    Toast.makeText(GetInformationActivity.this, "Please select your Height", Toast.LENGTH_SHORT).show();
                }

                if(editWeight.getText().toString().equals("")) {
                    Toast.makeText(GetInformationActivity.this, "Please enter your Weight", Toast.LENGTH_SHORT).show();
                }

                if(editStepGoal.getText().toString().equals("")) {
                    Toast.makeText(GetInformationActivity.this, "Please enter your Step Goal", Toast.LENGTH_SHORT).show();
                }

                if(weightGoal.getSelectedItem().toString().equals("I want to...")) {
                    Toast.makeText(GetInformationActivity.this, "Please select your Weight Goal", Toast.LENGTH_SHORT).show();
                }
                else {
                    user.setAge(Integer.parseInt(editAge.getText().toString()));
                    user.setGender(gender.getSelectedItem().toString());
                    int feet = Integer.parseInt(heightFeet.getSelectedItem().toString());
                    int inches = Integer.parseInt(heightInches.getSelectedItem().toString());
                    user.setHeight((feet*12) + inches);
                    user.setWeight(Integer.parseInt(editWeight.getText().toString()));
                    user.setStepGoal(Integer.parseInt(editStepGoal.getText().toString()));
                    user.setWeightGoal(weightGoal.getSelectedItemPosition());
                    user.setSetUpCompleted(true);

                    users.child(username).setValue(user);
                    Toast.makeText(GetInformationActivity.this, "Profile Setup Completed!", Toast.LENGTH_SHORT).show();

                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                    myIntent.putExtra("Username", username);
                    startActivity(myIntent);
                }

            }
        });
    }
}
