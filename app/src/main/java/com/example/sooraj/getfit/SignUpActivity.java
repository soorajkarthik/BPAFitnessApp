package com.example.sooraj.getfit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUpActivity extends AppCompatActivity {

    //TODO: DOCUMENTATION
    /**
     * Fields
     */
    private FirebaseDatabase database;
    private DatabaseReference users;
    private EditText editMail, editUsername, editPassword, editConfirmPassword;
    private Button btnSignUp, btnToLogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        editMail = findViewById(R.id.editMail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnToLogIn = findViewById(R.id.btnToLogIn);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User user = new User(editMail.getText().toString(),
                        editUsername.getText().toString(),
                        editPassword.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUsername()).exists()) {
                            Toast.makeText(SignUpActivity.this, "This Username is Taken!", Toast.LENGTH_SHORT).show();
                            editUsername.setText("");
                        } else if (user.getUsername().isEmpty() || user.getUsername().contains(" ") || user.getUsername().length() < 6) {
                            Toast.makeText(SignUpActivity.this, "That username is not valid", Toast.LENGTH_SHORT).show();
                            Toast.makeText(SignUpActivity.this, "Please select a username without any spaces with at least six characters", Toast.LENGTH_SHORT).show();
                        } else {

                            if (editConfirmPassword.getText().toString().equals(editPassword.getText().toString())) {
                                users.child(user.getUsername()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getApplicationContext(), GetInformationActivity.class);
                                myIntent.putExtra("username", user.getUsername());
                                startActivity(myIntent);

                            } else {
                                Toast.makeText(SignUpActivity.this, "Please ensure the password you have entered is consistent", Toast.LENGTH_SHORT).show();
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
