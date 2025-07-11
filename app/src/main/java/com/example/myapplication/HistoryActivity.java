package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> historyList;
    ArrayAdapter<String> adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listview);
        historyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text_item, historyList);
        listView.setAdapter(adapter);

        String Username = getIntent().getStringExtra("username");

        if(Username != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users").child(Username).child("history");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    historyList.clear();
                    for(DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        String label = dateSnapshot.child("labels").getValue(String.class);

                        String display = date + "\n" + label + "\n";
                        historyList.add(display);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HistoryActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                String item = historyList.get(position);
                String[] parts = item.split("\n");

                String date = parts[0], label = parts[1];

                reference.child(date)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String uri = snapshot.child("uriPic").getValue(String.class);
                                String advice = snapshot.child("advice").getValue(String.class);

                                Intent intent = new Intent(HistoryActivity.this, DetailHistoryActivity.class);
                                intent.putExtra("date", date);
                                intent.putExtra("label", label);
                                intent.putExtra("uri", uri);
                                intent.putExtra("advice", advice);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(HistoryActivity.this, "เกิดข้อผิดพลาดในการโหลดรูป", Toast.LENGTH_SHORT).show();
                            }
                        });

            });

        }


    }
}