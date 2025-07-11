package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ShowSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_setting);

        String username = getIntent().getStringExtra("username");

        TextView firstName = findViewById(R.id.firstName);
        TextView surName = findViewById(R.id.surName);
        TextView Age = findViewById(R.id.Age);
        TextView Weight = findViewById(R.id.Weight);
        TextView Height = findViewById(R.id.Height);
        TextView Bmi = findViewById(R.id.Bmi);
        TextView bmiText = findViewById(R.id.bmiText);
        Button editBtn = findViewById(R.id.EditBtn);

        if(username != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users").child(username).child("personal");

            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String firstname = snapshot.child("firstname").getValue(String.class);
                        String surname = snapshot.child("surname").getValue(String.class);
                        Integer age = snapshot.child("age").getValue(Integer.class);
                        Float weight = snapshot.child("weight").getValue(Float.class);
                        Float height = snapshot.child("height").getValue(Float.class);
                        float bmi = 0;
                        String text;

                        if(weight != null && height != null) {
                            bmi = weight / (height/100 * height/100);

                            if(bmi < 18.5) text = "ต่ำกว่าเกณฑ์";
                            else if(bmi < 23) text = "ปกติสมส่วน";
                            else if(bmi < 25) text = "น้ำหนักเกิน";
                            else if(bmi < 30) text = "อ้วนระดับ 1";
                            else text = "อ้วนระดับ 2";

                            firstName.setText(firstname);
                            surName.setText(surname);
                            Age.setText(String.valueOf(age));
                            Weight.setText(String.valueOf(weight));
                            Height.setText(String.valueOf(height));
                            Bmi.setText(String.format("%.2f", bmi));
                            bmiText.setText(text);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ShowSettingActivity.this, SettingActivity.class);
            intent.putExtra("from", "show");
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }
}