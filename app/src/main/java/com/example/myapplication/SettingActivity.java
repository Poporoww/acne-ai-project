package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        String username = getIntent().getStringExtra("username");
        String from = getIntent().getStringExtra("from");

        EditText firstname = findViewById(R.id.firstName);
        EditText surname = findViewById(R.id.surName);
        EditText Age = findViewById(R.id.Age);
        EditText Weight = findViewById(R.id.Weight);
        EditText Height = findViewById(R.id.Height);
        Button saveBtn = findViewById(R.id.Save);

        saveBtn.setOnClickListener(v -> {
            String first = firstname.getText().toString().trim();
            String sur = surname.getText().toString().trim();
            String age = Age.getText().toString().trim();
            String weight = Weight.getText().toString().trim();
            String height = Height.getText().toString().trim();

            if(first.isEmpty() || sur.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
                Toast.makeText(this, "Please Enter Completely", Toast.LENGTH_SHORT).show();
            } else {
                int a = Integer.parseInt(age);
                float w = Float.parseFloat(weight);
                float h = Float.parseFloat(height);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("users").child(username);

                PersonalHelper personalHelper = new PersonalHelper(first, sur, a, w, h);
                reference.child("personal").setValue(personalHelper);

                if(Objects.equals(from, "signup")) {
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SettingActivity.this, ShowSettingActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }

            }

        });

    }
}