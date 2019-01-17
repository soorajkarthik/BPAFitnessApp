package com.example.sooraj.getfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment extends Fragment {

    //Fields
    private FirebaseDatabase database;
    private DatabaseReference users;
    private View view;
    private EditText editNewWeight, editAge, editStepGoal;
    private Spinner spinnerGender, spinnerHeightInches, spinnerHeightFeet, spinnerActivityLevel, spinnerWeightGoal;
    private TextView textEditProfile, textFeet, textInches, textStepGoal;
    private Button buttonAddWeight;
    private String username;
    private User user;
    private boolean editingEnabled = false;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);


        //Get reference to database and the current user
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        //Get references to views
        textEditProfile = view.findViewById(R.id.textEditProfile);
        editNewWeight = view.findViewById(R.id.editNewWeight);
        editAge = view.findViewById(R.id.editAge);
        editStepGoal = view.findViewById(R.id.editStepGoal);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerHeightInches = view.findViewById(R.id.spinnerHeightInches);
        spinnerHeightFeet = view.findViewById(R.id.spinnerHeightFeet);
        spinnerActivityLevel = view.findViewById(R.id.spinnerActivityLevel);
        spinnerWeightGoal = view.findViewById(R.id.weightGoal);
        buttonAddWeight = view.findViewById(R.id.buttonConfirmNewWeight);
        textFeet = view.findViewById(R.id.textFeet);
        textInches = view.findViewById(R.id.textInches);
        textStepGoal = view.findViewById(R.id.textStepGoal);

        //Set values to what are currently stored in Firebase for current user
        editAge.setText(user.getAge() + "");
        editStepGoal.setText(user.getStepGoal() + "");

        if (user.getGender().equalsIgnoreCase("Male")) {
            spinnerGender.setSelection(1);
        } else {
            spinnerGender.setSelection(2);
        }

        spinnerHeightFeet.setSelection(user.getHeight() / 12);
        spinnerHeightInches.setSelection((user.getHeight() % 12) + 1);
        spinnerActivityLevel.setSelection(user.getActivityLevel() + 1);
        spinnerWeightGoal.setSelection(user.getWeightGoal() + 1);

        /*
         * Check to see if user's weight has changed (if the weight they entered is not the same as the weight stored in Firebase
         * Updates user's weight in Firebase if the entered weight is different
         * Updates user's calorie goal and macro-nutrient requirements based on their new weight
         */
        buttonAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNewWeight.getText().toString().equals(user.getWeight() + "")) {
                    Toast.makeText(getActivity(), "This weight is your current weight", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Please only add new weights once your weight has changed", Toast.LENGTH_SHORT).show();
                } else if (!editNewWeight.getText().toString().isEmpty()) {

                    int newWeight = Integer.parseInt(editNewWeight.getText().toString());


                    int age = Integer.parseInt(editAge.getText().toString());
                    int height = Integer.parseInt(spinnerHeightFeet.getSelectedItem().toString()) * 12 + Integer.parseInt(spinnerHeightInches.getSelectedItem().toString());
                    String genderString = spinnerGender.getSelectedItem().toString();
                    int weightGoalInt = spinnerWeightGoal.getSelectedItemPosition() - 1;
                    int activityLevelInt = spinnerActivityLevel.getSelectedItemPosition() - 1;
                    int bmr = genderString.equals("Male") ? (int) (66 + (6.3 * newWeight) + (12.9 * height) - (6.8 * age)) : (int) (655 + (4.3 * newWeight) + (4.7 * height) - (4.7 * age));
                    double bmi = ((double) newWeight / (height * height)) * 703;

                    calculateCalorieGoal(weightGoalInt, activityLevelInt, bmr);

                    user.setWeight(newWeight);
                    user.setBmi(bmi);
                    users.child(username).setValue(user);
                    editNewWeight.setText("");
                    Toast.makeText(getActivity(), "Your new weight has been set!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Enter your new weight!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setFocusEditingDisabled();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Logs out user and deletes the stored username from device
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        /*
         * Check to see whether or not profile editing is enabled
         * If disabled, then enable profile editing
         * If enabled, check to see if all values are valid, and if so, update the user in Firebase
         */

        else if (item.getItemId() == R.id.action_profile && !editingEnabled) {
            Toast.makeText(getActivity(), "Edit your profile", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Click \"Check\" to confirm ", Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.done_white_24dp);
            setFocusEditingEnabled();

        } else if (item.getItemId() == R.id.action_profile && editingEnabled) {


            //Check to make sure all values are valid

            if (editAge.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter your Age", Toast.LENGTH_SHORT).show();
            } else if (spinnerGender.getSelectedItem().toString().equals("Sex")) {
                Toast.makeText(getActivity(), "Please select your Sex", Toast.LENGTH_SHORT).show();
            } else if (spinnerHeightFeet.getSelectedItem().toString().equals("Height") || spinnerHeightInches.getSelectedItem().toString().equals("Height")) {
                Toast.makeText(getActivity(), "Please select your Height", Toast.LENGTH_SHORT).show();
            } else if (editStepGoal.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter your Step Goal", Toast.LENGTH_SHORT).show();
            } else if (spinnerWeightGoal.getSelectedItem().toString().equals("Weight Goal")) {
                Toast.makeText(getActivity(), "Please select your Weight Goal", Toast.LENGTH_SHORT).show();
            } else if (spinnerActivityLevel.getSelectedItem().toString().equals("Activity Level")) {
                Toast.makeText(getActivity(), "Please select your Activity Level", Toast.LENGTH_SHORT).show();
            }

            //Update the user in Firebase
            else {

                int age = Integer.parseInt(editAge.getText().toString());
                int height = Integer.parseInt(spinnerHeightFeet.getSelectedItem().toString()) * 12 + Integer.parseInt(spinnerHeightInches.getSelectedItem().toString());
                int weight = user.getWeight();
                double bmi = ((double) weight / (height * height)) * 703;
                int stepGoal = Integer.parseInt(editStepGoal.getText().toString());
                String genderString = spinnerGender.getSelectedItem().toString();
                int weightGoalInt = spinnerWeightGoal.getSelectedItemPosition() - 1;
                int activityLevelInt = spinnerActivityLevel.getSelectedItemPosition() - 1;
                int bmr = genderString.equals("Male") ? (int) (66 + (6.3 * weight) + (12.9 * height) - (6.8 * age)) : (int) (655 + (4.3 * weight) + (4.7 * height) - (4.7 * age));

                calculateCalorieGoal(weightGoalInt, activityLevelInt, bmr);

                /*
                 * Update local current user reference
                 * Update current user in Firebase
                 */
                user.setAge(age);
                user.setGender(genderString);
                user.setHeight(height);
                user.setStepGoal(stepGoal);
                user.setWeightGoal(weightGoalInt);
                user.setBmi(bmi);
                user.setActivityLevel(activityLevelInt);

                users.child(username).setValue(user);

                Toast.makeText(getActivity(), "Your changes were saved!", Toast.LENGTH_SHORT).show();
                setFocusEditingDisabled();
                item.setIcon(R.drawable.edit_white_24dp);

            }

        }
        return true;
    }

    public void calculateCalorieGoal(int weightGoalInt, int activityLevelInt, int bmr) {

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

        //Calculate macro-nutrients
        int carbs = (int) (calorieGoal * 0.4 / 4);
        int fat = (int) (calorieGoal * .3 / 9);
        int protein = (int) (calorieGoal * .3 / 4);

        user.setCalorieGoal(calorieGoal);
        user.setCarbGoal(carbs);
        user.setFatGoal(fat);
        user.setProteinGoal(protein);
    }

    /*
     * Disables profile editing
     * Allows the user to enter new weights
     */
    public void setFocusEditingDisabled() {
        editNewWeight.setEnabled(true);
        buttonAddWeight.setEnabled(true);
        editAge.setEnabled(false);
        editStepGoal.setEnabled(false);
        spinnerGender.setEnabled(false);
        spinnerHeightInches.setEnabled(false);
        spinnerHeightFeet.setEnabled(false);
        spinnerActivityLevel.setEnabled(false);
        spinnerWeightGoal.setEnabled(false);

        editNewWeight.setFocusableInTouchMode(true);
        buttonAddWeight.setFocusable(true);
        editAge.setFocusableInTouchMode(false);
        editStepGoal.setFocusableInTouchMode(false);
        spinnerGender.setFocusable(false);
        spinnerHeightInches.setFocusable(false);
        spinnerHeightFeet.setFocusable(false);
        spinnerActivityLevel.setFocusable(false);
        spinnerWeightGoal.setFocusable(false);

        //Make text opacity consistent
        textFeet.setAlpha(0.38f);
        textInches.setAlpha(0.38f);
        textStepGoal.setAlpha(0.38f);

        textEditProfile.setText("Your Profile");
        editingEnabled = false;
    }

    /*
     * Enables profile editing
     * Prevents the user from adding new weights
     */
    public void setFocusEditingEnabled() {

        editNewWeight.setEnabled(false);
        buttonAddWeight.setEnabled(false);
        editAge.setEnabled(true);
        editStepGoal.setEnabled(true);
        spinnerGender.setEnabled(true);
        spinnerHeightInches.setEnabled(true);
        spinnerHeightFeet.setEnabled(true);
        spinnerActivityLevel.setEnabled(true);
        spinnerWeightGoal.setEnabled(true);

        editNewWeight.setFocusableInTouchMode(false);
        buttonAddWeight.setFocusable(false);
        editAge.setFocusableInTouchMode(true);
        editStepGoal.setFocusableInTouchMode(true);
        spinnerGender.setFocusable(true);
        spinnerHeightInches.setFocusable(true);
        spinnerHeightFeet.setFocusable(true);
        spinnerActivityLevel.setFocusable(true);
        spinnerWeightGoal.setFocusable(true);

        //Make text opacity consistent
        textFeet.setAlpha(0.87f);
        textInches.setAlpha(0.87f);
        textStepGoal.setAlpha(0.87f);

        textEditProfile.setText("Edit Profile");
        editingEnabled = true;
    }
}
