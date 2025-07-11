package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);

        String date = getIntent().getStringExtra("date");
        String label = getIntent().getStringExtra("label");
        String uri = getIntent().getStringExtra("uri");
        String advice = getIntent().getStringExtra("advice");

        TextView hisDate = findViewById(R.id.hisDate);
        TextView hisLabel = findViewById(R.id.hisLabel);
        ImageView hisPic = findViewById(R.id.hisPic);
        TextView hisAdvice = findViewById(R.id.hisAdvice);

        hisDate.setText(date);
        hisLabel.setText(label);
        hisPic.setImageURI(Uri.parse(uri));
        hisAdvice.setText(advice);
    }
}