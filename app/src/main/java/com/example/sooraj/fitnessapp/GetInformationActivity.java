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

public class GetInformationActivity extends AppCompatActivity {

    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;
    User user;

    Button confirmButton;
    EditText editAge, editWeight, editStepGoal;
    Spinner gender, heightFeet, heightInches, weightGoal, activityLevel;


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
        editWeight = findViewById(R.id.editNewWeight);
        editStepGoal = findViewById(R.id.editStepGoal);
        gender = findViewById(R.id.spinnerGender);
        heightFeet = findViewById(R.id.spinnerHeightFeet);
        heightInches = findViewById(R.id.spinnerHeightInches);
        weightGoal = findViewById(R.id.weightGoal);
        activityLevel = findViewById(R.id.spinnerActivityLevel);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editAge.getText().toString().equals("")) {
                    Toast.makeText(GetInformationActivity.this, "Please enter your Age", Toast.LENGTH_SHORT).show();
                }

                if (gender.getSelectedItem().toString().equals("Sex")) {
                    Toast.makeText(GetInformationActivity.this, "Please select your Sex", Toast.LENGTH_SHORT).show();
                }

                if (heightFeet.getSelectedItem().toString().equals("Height") || heightInches.getSelectedItem().toString().equals("Height")) {
                    Toast.makeText(GetInformationActivity.this, "Please select your Height", Toast.LENGTH_SHORT).show();
                }

                if (editWeight.getText().toString().equals("")) {
                    Toast.makeText(GetInformationActivity.this, "Please enter your Weight", Toast.LENGTH_SHORT).show();
                }

                if (editStepGoal.getText().toString().equals("")) {
                    Toast.makeText(GetInformationActivity.this, "Please enter your Step Goal", Toast.LENGTH_SHORT).show();
                }

                if (weightGoal.getSelectedItem().toString().equals("Weight Goal")) {
                    Toast.makeText(GetInformationActivity.this, "Please select your Weight Goal", Toast.LENGTH_SHORT).show();
                }

                if (activityLevel.getSelectedItem().toString().equals("Activity Level")) {
                    Toast.makeText(GetInformationActivity.this, "Please select your Weight Goal", Toast.LENGTH_SHORT).show();
                } else {

                    int age = Integer.parseInt(editAge.getText().toString());
                    int height = Integer.parseInt(heightFeet.getSelectedItem().toString()) * 12 + Integer.parseInt(heightInches.getSelectedItem().toString());
                    int weight = Integer.parseInt(editWeight.getText().toString());
                    double bmi = ((double) weight / (height * height)) * 703;
                    String genderString = gender.getSelectedItem().toString();
                    int stepGoal = Integer.parseInt(editStepGoal.getText().toString());
                    int weightGoalInt = weightGoal.getSelectedItemPosition() - 1;
                    int activityLevelInt = activityLevel.getSelectedItemPosition() - 1;
                    int bmr = genderString.equals("Male") ? (int) (66 + (6.3 * weight) + (12.9 * height) - (6.8 * age)) : (int) (655 + (4.3 * weight) + (4.7 * height) - (4.7 * age));
                    int calorieGoal;

                    switch (activityLevelInt) {
                        case 0:
                            calorieGoal = (int) (bmr * 1.2);
                            break;
                        case 1:
                            calorieGoal = (int) (bmr * 1.375);
                            break;
                        case 2:
                            calorieGoal = (int) (bmr * 1.55);
                            break;
                        case 3:
                            calorieGoal = (int) (bmr * 1.725);
                            break;
                        case 4:
                            calorieGoal = (int) (bmr * 1.9);
                            break;
                        default:
                            calorieGoal = bmr;
                            break;
                    }

                    switch (weightGoalInt) {
                        case 0:
                            calorieGoal -= 400;
                            break;
                        case 2:
                            calorieGoal += 250;
                            break;
                        default:
                            break;
                    }

                    int carbs = (int) (calorieGoal * 0.4 / 4);
                    int fat = (int) (calorieGoal * .3 / 9);
                    int protein = (int) (calorieGoal * .3 / 4);


                    user.setAge(age);
                    user.setGender(genderString);
                    user.setHeight(height);
                    user.setWeight(weight);
                    user.setStepGoal(stepGoal);
                    user.setWeightGoal(weightGoalInt);
                    user.setBmi(bmi);
                    user.setActivityLevel(activityLevelInt);
                    user.setCalorieGoal(calorieGoal);
                    user.setCarbGoal(carbs);
                    user.setFatGoal(fat);
                    user.setProteinGoal(protein);
                    user.setSetUpCompleted(true);
                    //user.putStepsStorage(Calendar.getInstance().getTime().toString(), 1000);


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
