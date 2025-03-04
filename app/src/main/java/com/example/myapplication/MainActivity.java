package com.example.myapplication;

import static com.example.myapplication.R.id.bmi;
import static com.example.myapplication.R.id.search;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PICK = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    private String getUserfromLogin;
    DatabaseHelper databaseHelper;
    TextView bmiText, bmiNum, bmiShow, bmiAdvice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView helloText = findViewById(R.id.hello);
        databaseHelper = new DatabaseHelper(this);
        Button loginButton = findViewById(R.id.LoginHome), ggForm = findViewById(R.id.ggform);
        FrameLayout aboutMe = findViewById(R.id.me), history = findViewById(search);
        bmiText = findViewById(R.id.bmi_text_main);
        bmiNum = findViewById(R.id.bmi_num_main);
        bmiShow = findViewById(R.id.bmi_show_main);
        bmiAdvice = findViewById(R.id.bmi_advice_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        }



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        ggForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://shorturl.at/Bn610";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        getUserfromLogin = getIntent().getStringExtra("USERNAME2");


        if(getUserfromLogin != null && !getUserfromLogin.isEmpty()) {
            loginButton.setVisibility(View.GONE);
            helloText.setVisibility(View.VISIBLE);
            helloText.setText("สวัสดี " + getUserfromLogin);

            Cursor cursor = databaseHelper.getuserData(getUserfromLogin);

            double weight, height, bmi = 0;
            if(cursor != null && cursor.moveToFirst()) {
                weight = cursor.getDouble(cursor.getColumnIndexOrThrow("WEIGHT"));
                height = cursor.getDouble(cursor.getColumnIndexOrThrow("HEIGHT"));
                bmi = weight / (height/100 * height/100);
            }
            assert cursor != null;
            cursor.close();

            String text, advice;
            if(bmi < 18.5) {
                text = "ต่ำกว่าเกณฑ์";
                advice = "- ควรเพิ่มปริมาณอาหารที่มีพลังงานและโปรตีนสูง เช่น เนื้อสัตว์ไม่ติดมัน ไข่ นม และถั่ว\n" +
                        "- รับประทานอาหารให้ครบ 5 หมู่ และเพิ่มมื้ออาหารว่างเพื่อเสริมพลังงาน\n" +
                        "- ออกกำลังกายเพื่อเสริมสร้างกล้ามเนื้อ เช่น เวทเทรนนิ่ง หรือโยคะ";
            }
            else if(bmi < 23) {
                text = "ปกติสมส่วน";
                advice = "- ควรรักษาสมดุลของอาหารและออกกำลังกายอย่างสม่ำเสมอ\n" +
                        "- เลือกรับประทานอาหารที่มีประโยชน์ เช่น ผัก ผลไม้ โปรตีนไขมันต่ำ และคาร์โบไฮเดรตเชิงซ้อน\n" +
                        "- ดื่มน้ำให้เพียงพอและพักผ่อนอย่างเหมาะสม";
            }
            else if(bmi < 25) {
                text = "น้ำหนักเกิน";
                advice = "- ลดปริมาณอาหารที่มีไขมันและน้ำตาลสูง หลีกเลี่ยงของทอดและเครื่องดื่มหวาน\n" +
                        "- เพิ่มการออกกำลังกาย เช่น เดินเร็ว วิ่ง หรือปั่นจักรยาน อย่างน้อย 150 นาทีต่อสัปดาห์\n" +
                        "- ควบคุมปริมาณแคลอรีต่อวันให้เหมาะสมกับการใช้พลังงาน";
            }
            else if(bmi < 30) {
                text = "อ้วนระดับ 1";
                advice = "- ปรับพฤติกรรมการกินโดยเน้นอาหารที่มีไฟเบอร์สูง เช่น ผัก ผลไม้ และธัญพืชเต็มเมล็ด\n" +
                        "- ลดอาหารที่มีไขมันอิ่มตัว เช่น อาหารฟาสต์ฟู้ด และขนมขบเคี้ยว\n" +
                        "- เพิ่มกิจกรรมทางกาย เช่น การออกกำลังกายแบบคาร์ดิโอและเวทเทรนนิ่ง";
            }
            else {
                text = "อ้วนระดับ 2";
                advice = "- ควบคุมพลังงานที่ได้รับในแต่ละวันโดยลดปริมาณอาหารที่มีแคลอรีสูง\n" +
                        "- เพิ่มการออกกำลังกายอย่างต่อเนื่อง และอาจพิจารณาปรึกษาแพทย์หากต้องการลดน้ำหนักอย่างจริงจัง\n" +
                        "- ติดตามสุขภาพและภาวะแทรกซ้อน เช่น โรคเบาหวาน ความดันโลหิตสูง และไขมันในเลือดสูง";
            }

            bmiText.setVisibility(View.VISIBLE);
            bmiNum.setText(String.format("%.2f", bmi));
            bmiShow.setText(text);
            bmiAdvice.setText(advice);

        } else {
            loginButton.setVisibility(View.VISIBLE);
            helloText.setVisibility(View.GONE);
            bmiText.setVisibility(View.GONE);
        }

        aboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getUserfromLogin != null && !getUserfromLogin.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                    intent.putExtra("USER", getUserfromLogin);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getUserfromLogin != null && !getUserfromLogin.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    intent.putExtra("USERNAME2", getUserfromLogin);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.CAMERA
            }, 101);
        }

    }


    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }
    Uri picUri;

    public void takePhoto(View view) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "capture_image.jpg");
        picUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ตรวจสอบว่าเป็นการเลือกภาพจากแกลเลอรี
        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            Intent sendResult = new Intent(MainActivity.this, ResultActivity.class);
            sendResult.putExtra("imageUri", uri.toString()); // ส่ง URI แทน Bitmap
            sendResult.putExtra("USERNAME2", getUserfromLogin);
            startActivity(sendResult);
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && picUri != null) {

            Intent sendResult = new Intent(MainActivity.this, ResultActivity.class);
            sendResult.putExtra("imageUri", picUri.toString()); // ส่ง URI แทน Bitmap
            startActivity(sendResult);

        }

    }
}
