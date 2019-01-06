package com.example.sooraj.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference users;
    private SharedPreferences pref;
    private EditText editMail, editUsername, editPassword;
    private Button btnLogIn, btnToSignUp;
    private CheckBox checkStaySignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if (pref.getString("username", null) != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("username", pref.getString("username", null));
            startActivity(intent);
        }


        setContentView(R.layout.activity_login);


        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");


        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        btnToSignUp = findViewById(R.id.btnToSignUp);

        checkStaySignedIn = findViewById(R.id.checkStaySignedIn);


        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logIn(editUsername.getText().toString(),
                        editPassword.getText().toString());

            }
        });

        btnToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void logIn(final String username, final String password) {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(username).exists()) {
                    User login = dataSnapshot.child(username).getValue(User.class);

                    if (login.getPassword().equals(password)) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        if (checkStaySignedIn.isChecked()) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("username", username);
                            editor.commit();
                        }

                        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                        myIntent.putExtra("username", username);
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Username not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //custom code
            }
        });
    }
}
