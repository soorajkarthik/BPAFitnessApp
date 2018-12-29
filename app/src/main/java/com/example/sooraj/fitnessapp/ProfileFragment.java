package com.example.sooraj.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.sooraj.fitnessapp.Model.User;

import java.util.Calendar;
import java.util.Objects;


import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private View view;
    private EditText editNewWeight, editAge, editStepGoal;
    private Spinner spinnerGender, spinnerHeightInches, spinnerHeightFeet, spinnerActivityLevel, spinnerWeightGoal;
    private TextView textEditProfile;
    private Button buttonAddWeight;
    private String username;
    private User user;
    private boolean editingEnabled = false;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity)getActivity()).getUser();
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

        editAge.setText(user.getAge() + "");
        editStepGoal.setText(user.getStepGoal() + "");

        if(user.getGender().equalsIgnoreCase("Male")) {
            spinnerGender.setSelection(1);
        } else {
            spinnerGender.setSelection(2);
        }

        spinnerHeightFeet.setSelection(user.getHeight()/12);
        spinnerHeightInches.setSelection((user.getHeight()%12) + 1);
        spinnerActivityLevel.setSelection(user.getActivityLevel() + 1);
        spinnerWeightGoal.setSelection(user.getWeightGoal() + 1);

        buttonAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editNewWeight.getText().toString().equals(user.getWeight() + "")) {
                    Toast.makeText(getActivity(), "This weight is your current weight", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Please only add new weights once your weight has changed", Toast.LENGTH_SHORT).show();
                }

                else {

                    user.putWeightStorage(Calendar.getInstance().getTime().toString(), user.getWeight());
                    user.setWeight(Integer.parseInt(editNewWeight.getText().toString()));
                    users.child(username).child("weight").setValue(user.getWeight());
                    Toast.makeText(getActivity(), "Your new weight has been set!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editAge.setEnabled(false);
        editAge.setFocusable(false);
        editStepGoal.setEnabled(false);
        editStepGoal.setFocusable(false);
        spinnerGender.setEnabled(false);
        spinnerGender.setFocusable(false);
        spinnerHeightInches.setEnabled(false);
        spinnerHeightInches.setFocusable(false);
        spinnerHeightFeet.setEnabled(false);
        spinnerHeightFeet.setFocusable(false);
        spinnerActivityLevel.setEnabled(false);
        spinnerActivityLevel.setFocusable(false);
        spinnerWeightGoal.setEnabled(false);
        spinnerWeightGoal.setFocusable(false);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_profile && !editingEnabled) {
            Toast.makeText(getActivity(), "Edit your profile " + item.getTitle(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Click \"Check\" to confirm " + item.getTitle(), Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.done_white_24dp);
            editAge.setEnabled(true);
            editAge.setFocusable(true);
            editStepGoal.setEnabled(true);
            editStepGoal.setFocusable(true);
            spinnerGender.setEnabled(true);
            spinnerGender.setFocusable(true);
            spinnerHeightInches.setEnabled(true);
            spinnerHeightInches.setFocusable(true);
            spinnerHeightFeet.setEnabled(true);
            spinnerHeightFeet.setFocusable(true);
            spinnerActivityLevel.setEnabled(true);
            spinnerActivityLevel.setFocusable(true);
            spinnerWeightGoal.setEnabled(true);
            spinnerWeightGoal.setFocusable(true);
            textEditProfile.setText("Edit Profile");
            editingEnabled = true;
        } 
        
        else if (item.getItemId() == R.id.action_profile && editingEnabled) {
            item.setIcon(R.drawable.edit_white_24dp);

            editAge.setEnabled(false);
            editAge.setFocusable(false);
            editStepGoal.setEnabled(false);
            editStepGoal.setFocusable(false);
            spinnerGender.setEnabled(false);
            spinnerGender.setFocusable(false);
            spinnerHeightInches.setEnabled(false);
            spinnerHeightInches.setFocusable(false);
            spinnerHeightFeet.setEnabled(false);
            spinnerHeightFeet.setFocusable(false);
            spinnerActivityLevel.setEnabled(false);
            spinnerActivityLevel.setFocusable(false);
            spinnerWeightGoal.setEnabled(false);
            spinnerWeightGoal.setFocusable(false);

            if(editAge.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter your Age", Toast.LENGTH_SHORT).show();
            }

            if(spinnerGender.getSelectedItem().toString().equals("Sex")) {
                Toast.makeText(getActivity(), "Please select your Sex", Toast.LENGTH_SHORT).show();
            }

            if(spinnerHeightFeet.getSelectedItem().toString().equals("Height") || spinnerHeightInches.getSelectedItem().toString().equals("Height")) {
                Toast.makeText(getActivity(), "Please select your Height", Toast.LENGTH_SHORT).show();
            }

            if(editStepGoal.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter your Step Goal", Toast.LENGTH_SHORT).show();
            }

            if(spinnerWeightGoal.getSelectedItem().toString().equals("Weight Goal")) {
                Toast.makeText(getActivity(), "Please select your Weight Goal", Toast.LENGTH_SHORT).show();
            }

            if(spinnerActivityLevel.getSelectedItem().toString().equals("Activity Level")) {
                Toast.makeText(getActivity(), "Please select your Weight Goal", Toast.LENGTH_SHORT).show();
            }

            else {

                int age = Integer.parseInt(editAge.getText().toString());
                int height = Integer.parseInt(spinnerHeightFeet.getSelectedItem().toString()) * 12 + Integer.parseInt(spinnerHeightInches.getSelectedItem().toString());
                int weight = user.getWeight();
                double bmi = ((double)weight/(height*height)) * 703;
                String genderString = spinnerWeightGoal.getSelectedItem().toString();
                int stepGoal = Integer.parseInt(editStepGoal.getText().toString());
                int weightGoalInt = spinnerWeightGoal.getSelectedItemPosition()-1;
                int activityLevelInt = spinnerActivityLevel.getSelectedItemPosition()-1;
                int bmr = genderString.equals("Male") ?  (int)(66 + (6.3 *weight) + (12.9* height) - (6.8 * age)) :  (int)( 655 + (4.3*weight) + (4.7 * height) - (4.7 * age));
                int calorieGoal;

                switch (activityLevelInt)  {
                    case 0:
                        calorieGoal = (int)(bmr * 1.2);
                        break;
                    case 1:
                        calorieGoal = (int)(bmr*1.375);
                        break;
                    case 2:
                        calorieGoal = (int)(bmr*1.55);
                        break;
                    case 3:
                        calorieGoal = (int)(bmr*1.725);
                        break;
                    case 4:
                        calorieGoal = (int)(bmr*1.9);
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

                int carbs = (int)(calorieGoal * 0.4 / 4);
                int fat = (int) (calorieGoal *.3 / 9);
                int protein = (int) (calorieGoal *.3 / 4);

                user.setAge(age);
                user.setGender(genderString);
                user.setHeight(height);
                user.setStepGoal(stepGoal);
                user.setWeightGoal(weightGoalInt);
                user.setBmi(bmi);
                user.setActivityLevel(activityLevelInt);
                user.setCalorieGoal(calorieGoal);
                user.setCarbGoal(carbs);
                user.setFatGoal(fat);
                user.setProteinGoal(protein);

                users.child(username).setValue(user);
                Toast.makeText(getActivity(), "Your changes were saved!" + item.getTitle(), Toast.LENGTH_SHORT).show();
                textEditProfile.setText("Your Profile");
                editingEnabled = false;
                
            }
            
        }
        return true;
    }
}
