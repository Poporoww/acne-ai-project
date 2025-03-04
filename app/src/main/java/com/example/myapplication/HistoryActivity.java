package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HistoryActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ListView listView;
    String username;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceStata) {
        super.onCreate(savedInstanceStata);
        setContentView(R.layout.activity_history);

        databaseHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.historylistview);
        username = getIntent().getStringExtra("USERNAME2");

        showHistory();
    }

    private void showHistory() {
        cursor = databaseHelper.getHistory(username);
        if (cursor != null) {
            Log.d("DEBUG", "Cursor count: " + cursor.getCount()); // เช็คจำนวนข้อมูล
            while (cursor.moveToNext()) {
                Log.d("DEBUG", "DATA: " +
                        cursor.getString(cursor.getColumnIndexOrThrow("DATE")) + " - " +
                        cursor.getString(cursor.getColumnIndexOrThrow("RESULT")));
            }
        }
        String[] fromColumns = {"DATE", "RESULT"};
        int[] toViews = {R.id.date_text, R.id.result_text};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.history_item,
                cursor,
                fromColumns,
                toViews,
                0
        );

        if(username != null && !username.isEmpty()) {
            listView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor clickedItem = (Cursor) adapter.getItem(position);
                String result = clickedItem.getString(clickedItem.getColumnIndexOrThrow("RESULT"));
                String date = clickedItem.getString(clickedItem.getColumnIndexOrThrow("DATE"));
                String image = clickedItem.getString(clickedItem.getColumnIndexOrThrow("PIC"));
                String advice = clickedItem.getString(clickedItem.getColumnIndexOrThrow("ADVICE"));


                Intent intent = new Intent(HistoryActivity.this, ShowHistoryActivity.class);
                intent.putExtra("PIC_H", image);
                intent.putExtra("RESULT_H", result);
                intent.putExtra("DATE_H", date);
                intent.putExtra("ADVICE_H", advice);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();  // ปิด cursor เมื่อ Activity ถูกทำลาย
        }
    }
}
