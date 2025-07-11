package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResultActivity extends AppCompatActivity {

    ImageView resultPicture;
    TextView resultLabel, adviceText;
    Yolov5TFLiteDetector yolov5TFLiteDetector;
    Paint boxPaint = new Paint();
    Paint textPaint = new Paint();
    Bitmap bitmap;
    StringBuilder labelBuilder;
    Uri detectedUriPic;

    // Gemini Api Key
    private static final String API_KEY = "AIzaSyBXyT5YY6LL0TePXcpu22SsydBU_SJPTa4";
    String advice;
    String timestamp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultPicture = findViewById(R.id.resultPic);
        resultLabel = findViewById(R.id.resultLabel);
        adviceText = findViewById(R.id.adviceText);

        yolov5TFLiteDetector = new Yolov5TFLiteDetector();
        yolov5TFLiteDetector.setModelFile("best-fp16.tflite");
        yolov5TFLiteDetector.initialModel(this);

        boxPaint.setStrokeWidth(5);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setColor(Color.RED);

        textPaint.setStrokeWidth(50);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.GREEN);

        String ImageStr = getIntent().getStringExtra("imageUri");
        Uri ImageUri;
        if (ImageStr != null) {
            ImageUri = Uri.parse(ImageStr);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageUri);
                bitmap = rotateBitmapIfRequired(this, ImageUri, bitmap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ArrayList<Recognition> recognitions = yolov5TFLiteDetector.detect(bitmap);
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        ArrayList<String> resultList = new ArrayList<>();

        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Map<String, Boolean> mp = new HashMap<>();
        labelBuilder = new StringBuilder();

        for (Recognition recognition : recognitions) {
            String recog = recognition.getLabelName();
            if (recognition.getConfidence() > 0.5) {
                RectF location = recognition.getLocation();
                canvas.drawRect(recognition.getLocation(), boxPaint);
                canvas.drawText(recog + ":" + recognition.getConfidence(), location.left, location.top, textPaint);

                if (!mp.containsKey(recog)) {
                    mp.put(recog, true);
                    resultList.add(recog);
                }
            }
        }

        if (resultList.isEmpty()) {
            labelBuilder.append("Non result eiei...");
        } else {
            labelBuilder.append(String.join(", ", resultList));
            getAdviceFromGemini(resultList);
        }

        detectedUriPic = getImageUri(this, mutableBitmap);

        resultPicture.setImageURI(detectedUriPic);
        resultLabel.setText(labelBuilder.toString());

    }

    // save result to database [ function ]
    private void saveToDatabase(Uri uriPic, StringBuilder labels, String advice) {

        String username = getIntent().getStringExtra("username");

        if(username != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users").child(username);

            HistoryHelper historyHelper = new HistoryHelper(detectedUriPic.toString(), labelBuilder.toString(), advice);

            reference.child("history").child(timestamp).setValue(historyHelper);

        }
    }

    // get advice from gemini [ function ]
    private void getAdviceFromGemini(ArrayList<String> detectedLabels) {
        String prompt = "ฉันพบสิ่งเหล่านี้บนใบหน้าของฉัน: " + String.join(", ", detectedLabels) +
                " คุณช่วยให้คำแนะนำเกี่ยวกับวิธีดูแลผิวและรักษาสิวให้ฉันได้ไหม? กรุณาตอบกระชับและไม่อยู่ในรูปแบบ Markdown";

        GeminiRequest request = new GeminiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

        RetrofitClient.getInstance().generateContent(url, request)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            List<Candidate> candidates = response.body().getCandidates();
                            if (candidates != null && !candidates.isEmpty()) {
                                Content content = candidates.get(0).getContent();
                                if(content != null && content.getParts() != null && !content.getParts().isEmpty()) {
                                    advice = content.getParts().get(0).getText();
                                    advice = advice.replace("*", "");
                                    adviceText.setText(advice);
                                    saveToDatabase(detectedUriPic, labelBuilder, advice);
                                    return;
                                }
                            }
                        } else {
                            advice = "Error : " + response.code();
                            adviceText.setText(advice);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeminiResponse> call, Throwable t) {
                        advice = "Request Failed : " + t.getMessage();
                        adviceText.setText(advice);
                    }
                });
    }

    // change bitmap to uri [ function ]
    private Uri getImageUri(Context context, Bitmap bitmap) {
        File storageDir = new File (context.getExternalFilesDir(null), "Pictures");
        if(!storageDir.exists()) {
            storageDir.mkdir();
        }

        File imageFile = new File(storageDir, "detect_image_" + System.currentTimeMillis() + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", imageFile);
    }

    // Rotate picture for capture ( if required ) [ function ]
    private Bitmap rotateBitmapIfRequired(Context context, Uri uri, Bitmap bitmap) throws IOException {
        @SuppressLint("Recycle")
        InputStream input = context.getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(input);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
