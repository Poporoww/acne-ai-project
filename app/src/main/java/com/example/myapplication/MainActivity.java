package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int REQUEST_PERMISSION = 300;
    String Username;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout popUpChooser = findViewById(R.id.PopUpChooser);
        ImageView pickImage = findViewById(R.id.pickImage);
        Button CameraButton = findViewById(R.id.camera);
        Button GalleryButton = findViewById(R.id.gallery), CloseFrame = findViewById(R.id.closeFrame);
        Button LoginHomeButton = findViewById(R.id.LoginHomeButton);
        RelativeLayout HistoryButton = findViewById(R.id.historyButton);
        RelativeLayout MeButton = findViewById(R.id.meButton);
        TextView helloName = findViewById(R.id.helloUsername);
        TextView showBmi = findViewById(R.id.bmi), bmiText = findViewById(R.id.bmiText);
        Button Question = findViewById(R.id.question);

        popUpChooser.setVisibility(View.GONE);
        showBmi.setVisibility(View.GONE);
        bmiText.setVisibility(View.GONE);

        Username = getIntent().getStringExtra("username");

        if (!hasStoragePermission() || !hasCameraPermission()) {
            requestPermission();
        }

        if(Username != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users").child(Username).child("personal");

            LoginHomeButton.setVisibility(View.GONE);


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String firstName = snapshot.child("firstname").getValue(String.class);
                        Float weight = snapshot.child("weight").getValue(Float.class);
                        Float height = snapshot.child("height").getValue(Float.class);

                        if(weight != null && height != null) {
                            float bmi = weight / (height / 100 * height / 100);

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
                            showBmi.setVisibility(View.VISIBLE);
                            bmiText.setVisibility(View.VISIBLE);
                            helloName.setText("Hello, " + firstName);
                            showBmi.setText("Bmi : " +  String.format("%.2f", bmi) + "\n( " + text + " )");
                            bmiText.setText(advice);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        // gg form acne question
        Question.setOnClickListener(v -> {
            String url = "https://forms.gle/jB2YKhmtHAnnBiQa9";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        // Pick Image Button
        pickImage.setOnClickListener(v -> {
            popUpChooser.setVisibility(View.VISIBLE);

            GalleryButton.setOnClickListener(v1 -> {
                if(!hasStoragePermission()) {
                    requestPermission();
                }
                boolean check = hasStoragePermission();
                if(check) {
                    popUpChooser.setVisibility(View.GONE);
                    selectImage();
                }
            });

            CameraButton.setOnClickListener(v2 -> {
                if(!hasCameraPermission()) {
                    requestPermission();
                }
                boolean check = hasCameraPermission();
                if(check) {
                    popUpChooser.setVisibility(View.GONE);
                    captureImage();
                }
            });


            CloseFrame.setOnClickListener(v3 -> popUpChooser.setVisibility(View.GONE));

        });

        LoginHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // History Button
        HistoryButton.setOnClickListener(v -> {
            if(Username != null) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("username", Username);
                startActivity(intent);
            } else {
                Toast.makeText(this, "กรุณาลงชื่อเข้าใช้", Toast.LENGTH_SHORT).show();
            }
        });

        // Personal Information Button
        MeButton.setOnClickListener(v -> {
            if(Username != null) {
                Intent intent = new Intent(MainActivity.this, ShowSettingActivity.class);
                intent.putExtra("username", Username);
                startActivity(intent);
            } else {
                Toast.makeText(this, "กรุณาลงชื่อเข้าใช้", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Select or Capture an Image [ Function ]
    public void selectImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    Uri picUri;
    public void captureImage() {
        File file = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "capture_image.jpg");
        }
        picUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check Request from gallery
        if(requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Intent sendResult = new Intent(MainActivity.this, ResultActivity.class);
            assert uri != null;
            sendResult.putExtra("imageUri", uri.toString());
            sendResult.putExtra("username", Username);
            startActivity(sendResult);
        }

        // Check Request from camera
        if(requestCode == REQUEST_IMAGE_CAPTURE && picUri != null) {
            Intent sendResult = new Intent(MainActivity.this, ResultActivity.class);
            sendResult.putExtra("imageUri", picUri.toString());
            sendResult.putExtra("username", Username);
            startActivity(sendResult);
        }
    }

    // request permission function
    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasStoragePermission() {
        boolean check;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return check;
    }

    public void requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }
}