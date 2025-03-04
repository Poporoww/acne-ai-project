package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_setting);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String username = getIntent().getStringExtra("USER");
        Cursor cursor = databaseHelper.getuserData(username);

        TextView firstName = findViewById(R.id.showfirstname), surName = findViewById(R.id.showsurname),
                Age = findViewById(R.id.showage), Weight = findViewById(R.id.showweight),
                Height = findViewById(R.id.showheight), bmiNum = findViewById(R.id.showbmi), bmiText = findViewById(R.id.bmi);

        if(cursor != null && cursor.moveToFirst()) {
            String firstname = cursor.getString(cursor.getColumnIndexOrThrow("FIRSTNAME"));
            String surname = cursor.getString(cursor.getColumnIndexOrThrow("SURNAME"));
            int age = cursor.getInt(cursor.getColumnIndexOrThrow("AGE"));
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("WEIGHT"));
            double height = cursor.getDouble(cursor.getColumnIndexOrThrow("HEIGHT"));
            double bmi = weight / (height/100 * height/100);

            firstName.setText(firstname);
            surName.setText(surname);
            Age.setText(String.valueOf(age));
            Weight.setText(String.valueOf(weight));
            Height.setText(String.valueOf(height));
            bmiNum.setText(String.format("%.2f", bmi));

            String text;
            if(bmi < 18.5) text = "ต่ำกว่าเกณฑ์";
            else if(bmi < 23) text = "ปกติสมส่วน";
            else if(bmi < 25) text = "น้ำหนักเกิน";
            else if(bmi < 30) text = "อ้วนระดับ 1";
            else text = "อ้วนระดับ 2";

            bmiText.setText(text);


        }
        assert cursor != null;
        cursor.close();

        Button logOut = findViewById(R.id.logout);
        Button update = findViewById(R.id.re_insert_data);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowActivity.this, SettingActivity.class);
                intent.putExtra("USERNAME2", username);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });
    }

    private void LogOut() {
        Intent intent = new Intent(ShowActivity.this, MainActivity.class);
        intent.putExtra("USERNAME2", "");
        startActivity(intent);
    }
}
