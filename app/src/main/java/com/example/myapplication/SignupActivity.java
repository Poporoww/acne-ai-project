package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    EditText username, password;
    Button loginButton, signupButton;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.new_username);
        password = findViewById(R.id.new_password);
        signupButton = findViewById(R.id.signup_btn);
        loginButton = findViewById(R.id.haveuser);
        databaseHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if(user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(SignupActivity.this, SettingActivity.class);
                    intent.putExtra("USERNAME1", user);
                    intent.putExtra("PASSWORD1", pass);
                    startActivity(intent);
                }
            }
        });
    }
}
