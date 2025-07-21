package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText signupUsername, signupPassword;
    TextView redirectLogin;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsername = findViewById(R.id.signupUsername);
        signupPassword = findViewById(R.id.signupPassword);
        redirectLogin = findViewById(R.id.redirectLogin);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                if(username.isEmpty()) {
                    signupUsername.setError("Username cannot be empty");
                } else if(password.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                } else {
                    HelperClass helperClass = new HelperClass(password);
                    reference.child(username).setValue(helperClass);

                    Toast.makeText(SignupActivity.this, "SignUp successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignupActivity.this, SettingActivity.class);
                    intent.putExtra("from", "signup");
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });

        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
