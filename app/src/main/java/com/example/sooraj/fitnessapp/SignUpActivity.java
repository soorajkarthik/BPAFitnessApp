package com.example.sooraj.fitnessapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class SignUpActivity extends AppCompatActivity {

    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;

    EditText editMail, editUsername, editPassword, editConfirmPassword;
    Button btnSignUp, btnToLogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        editMail = (EditText) findViewById(R.id.editMail);
        editUsername = (EditText) findViewById(R.id.editUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnToLogIn = (Button) findViewById(R.id.btnToLogIn);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User user = new User(editMail.getText().toString(),
                        editUsername.getText().toString(),
                        editPassword.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user.getUsername()).exists()) {
                            Toast.makeText(SignUpActivity.this, "This Username is Taken!", Toast.LENGTH_SHORT).show();
                        } else {
                            
                            if(editConfirmPassword.getText().toString().equals(editPassword.getText().toString())) {
                                users.child(user.getUsername()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Please ensure the password you have entered is consistant", Toast.LENGTH_SHORT).show();
                                editConfirmPassword.setText("");
                            }
                             
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //add custom code
                    }
                });
            }
        });

        btnToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myIntent);
            }
        });
    }

}
