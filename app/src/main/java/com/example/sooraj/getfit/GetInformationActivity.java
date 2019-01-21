package com.example.sooraj.getfit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GetInformationActivity extends AppCompatActivity {

    /**
     * Fields
     */
    private FirebaseDatabase database;
    private DatabaseReference users;
    private User user;
    private Button confirmButton;
    private EditText editAge, editWeight, editStepGoal;
    private Spinner gender, heightFeet, heightInches, weightGoal, activityLevel;

    /**
     * Get reference to Firebase Database, and the "Users" node
     * Get reference to current user from the username contained in the intent used to start this activity
     * Get reference to all components of the activity's view
     * @param savedInstanceState the last saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getinformation);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        final String username = Objects.requireNonNull(getIntent().getExtras()).getString("username");


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirmButton = findViewById(R.id.buttonConfirmProfile);
        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editNewWeight);
        editStepGoal = findViewById(R.id.editStepGoal);
        gender = findViewById(R.id.spinnerGender);
        heightFeet = findViewById(R.id.spinnerHeightFeet);
        heightInches = findViewById(R.id.spinnerHeightInches);
        weightGoal = findViewById(R.id.weightGoal);
        activityLevel = findViewById(R.id.spinnerActivityLevel);


        confirmButton.setOnClickListener(new View.OnClickListener() {

            /**
             * When button is pressed, check to see if all values are valid
             * If values are valid, update the user in Firebase
             * @param view view of the pressed button
             */
            @Override
            public void onClick(View view) {
                if (editAge.getText().toString().equals("")) {

                    Toast.makeText(GetInformationActivity.this,
                            "Please enter your Age",
                            Toast.LENGTH_SHORT).show();
                }

                if (gender.getSelectedItem().toString().equals("Sex")) {

                    Toast.makeText(GetInformationActivity.this,
                            "Please select your Sex",
                            Toast.LENGTH_SHORT).show();
                }

                if (heightFeet.getSelectedItem().toString().equals("Height") ||
                        heightInches.getSelectedItem().toString().equals("Height")) {

                    Toast.makeText(GetInformationActivity.this,
                            "Please select your Height",
                            Toast.LENGTH_SHORT).show();
                }

                if (editWeight.getText().toString().equals("")) {

                    Toast.makeText(GetInformationActivity.this,
                            "Please enter your Weight",
                            Toast.LENGTH_SHORT).show();
                }

                if (editStepGoal.getText().toString().equals("")) {

                    Toast.makeText(GetInformationActivity.this,
                            "Please enter your Step Goal",
                            Toast.LENGTH_SHORT).show();
                }

                if (weightGoal.getSelectedItem().toString().equals("Weight Goal")) {

                    Toast.makeText(GetInformationActivity.this,
                            "Please select your Weight Goal",
                            Toast.LENGTH_SHORT).show();
                }

                if (activityLevel.getSelectedItem().toString().equals("Activity Level")) {

                    Toast.makeText(GetInformationActivity.this,
                            "Please select your Weight Goal",
                            Toast.LENGTH_SHORT).show();
                }

                else {

                    int age = Integer.parseInt(editAge.getText().toString());

                    int feet = Integer.parseInt(heightFeet.getSelectedItem().toString());
                    int inches = Integer.parseInt(heightInches.getSelectedItem().toString());
                    int height =  (feet * 12) + inches;

                    int weight = Integer.parseInt(editWeight.getText().toString());
                    double bmi = ((double) weight / (height * height)) * 703;
                    String genderString = gender.getSelectedItem().toString();
                    int stepGoal = Integer.parseInt(editStepGoal.getText().toString());
                    int weightGoalInt = weightGoal.getSelectedItemPosition() - 1; //see User class for more details
                    int activityLevelInt = activityLevel.getSelectedItemPosition() - 1; //see User class for more details

                    //Calculate basal metabolic rate (bmr) using different formulas based on the user's entered sex
                    int bmr = genderString.equals("Male") ?
                            (int) (66 + (6.3 * weight) + (12.9 * height) - (6.8 * age)) :
                            (int) (655 + (4.3 * weight) + (4.7 * height) - (4.7 * age));

                    int calorieGoal;

                    //Calculate calories required to maintain current weight based on activity level
                    switch (activityLevelInt) {
                        //Little to no activity
                        case 0:
                            calorieGoal = (int) (bmr * 1.2);
                            break;
                        //Light exercise/sports 1–3 days/week
                        case 1:
                            calorieGoal = (int) (bmr * 1.375);
                            break;
                        //Moderate exercise/sports 3–5 days/week
                        case 2:
                            calorieGoal = (int) (bmr * 1.55);
                            break;
                        //Hard exercise/sports 6–7 days a week
                        case 3:
                            calorieGoal = (int) (bmr * 1.725);
                            break;
                        //Very hard exercise/sports and/or physical job
                        case 4:
                            calorieGoal = (int) (bmr * 1.9);
                            break;
                        default:
                            calorieGoal = bmr;
                            break;
                    }

                    //Tailor calorie goal based on weight goal
                    switch (weightGoalInt) {
                        //goal of losing weight
                        case 0:
                            calorieGoal -= 400;
                            break;
                        //goal of maintaining current weight
                        case 1:
                            break;
                        //goal of gaining muscle
                        case 2:
                            calorieGoal += 250;
                        default:
                            break;
                    }

                    //calculate macro-nutrients based on calorie goal
                    int carbs = (int) (calorieGoal * 0.4 / 4);
                    int fat = (int) (calorieGoal * .3 / 9);
                    int protein = (int) (calorieGoal * .3 / 4);

                    //Update local user reference
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

                    //Update user in Firebase
                    users.child(username).setValue(user);

                    Toast.makeText(GetInformationActivity.this,
                            "Profile Setup Completed!",
                            Toast.LENGTH_SHORT).show();

                    //Start MainActivity
                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                    myIntent.putExtra("username", username);
                    startActivity(myIntent);
                }

            }
        });
    }
}
