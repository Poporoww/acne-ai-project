package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class SettingActivity extends AppCompatActivity {

    EditText firstname, surname, age, weight, height;
    Button saveButton;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firstname = findViewById(R.id.firstname);
        surname = findViewById(R.id.surname);
        age = findViewById(R.id.age);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        saveButton = findViewById(R.id.savesetting);
        databaseHelper = new DatabaseHelper(this);

        String user = getIntent().getStringExtra("USERNAME2");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = firstname.getText().toString().trim();
                String sur = surname.getText().toString().trim();
                String ageStr = age.getText().toString().trim();
                String weightStr = weight.getText().toString().trim();
                String heightStr = height.getText().toString().trim();


                if (first.isEmpty() || sur.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
                    Toast.makeText(SettingActivity.this, "Please Enter Completely", Toast.LENGTH_SHORT).show();
                } else {
                    int a = Integer.parseInt(ageStr);
                    double w = Double.parseDouble(weightStr);
                    double h = Double.parseDouble(heightStr);
                    String username = getIntent().getStringExtra("USERNAME1"), password = getIntent().getStringExtra("PASSWORD1");


                    Cursor cursor = null;
                    if (user != null) cursor = databaseHelper.getuserData(user);

                    if (cursor != null && cursor.moveToFirst()) {
                        if (databaseHelper.updateData(user, first, sur, a, w, h)) {
                            Toast.makeText(SettingActivity.this, "UPDATE Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                            intent.putExtra("USERNAME2", user);
                            startActivity(intent);
                        }
                    } else {
                        if (databaseHelper.insertData(username, password, first, sur, a, w, h)) {
                            Toast.makeText(SettingActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                            intent.putExtra("USERNAME2", username);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

    }



}
