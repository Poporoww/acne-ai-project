package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.noties.markwon.Markwon;

public class ShowHistoryActivity extends AppCompatActivity {

    Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceStata) {
        super.onCreate(savedInstanceStata);
        setContentView(R.layout.activity_show_history);

        ImageView imageView = findViewById(R.id.pic_history);
        TextView resultTextView = findViewById(R.id.result_history);
        TextView dateTextView = findViewById(R.id.date_history);
        TextView adviceTextView = findViewById(R.id.advice_history);




        // รับข้อมูลจาก Intent
        String image = getIntent().getStringExtra("PIC_H");
        String result = getIntent().getStringExtra("RESULT_H");
        String date = getIntent().getStringExtra("DATE_H");
        String advice = getIntent().getStringExtra("ADVICE_H");



        if(image != null) imageView.setImageURI(Uri.parse(image));
        resultTextView.setText(result);
        dateTextView.setText(date);
        if(markwon != null) {
            assert advice != null;
            markwon.setMarkdown(adviceTextView, advice);
        }
        else adviceTextView.setText(advice);
    }
}
