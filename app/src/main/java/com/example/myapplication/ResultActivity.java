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

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {

    private static final String API_KEY = "YOUR_API_KEY";

    ImageView imageView;
    TextView resultTextView, adviceTextView;
    Bitmap bitmap;
    Yolov5TFLiteDetector yolov5TFLiteDetector;
    Paint boxPaint = new Paint();
    Paint textPaint = new Paint();
    String advice;
    Uri image;
    StringBuilder resultText;
    Markwon markwon;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        imageView = findViewById(R.id.images);
        resultTextView = findViewById(R.id.result_text);
        adviceTextView = findViewById(R.id.advice_text);

        yolov5TFLiteDetector = new Yolov5TFLiteDetector();
        yolov5TFLiteDetector.setModelFile("best-fp16.tflite");
        yolov5TFLiteDetector.initialModel(this);

        boxPaint.setStrokeWidth(5);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setColor(Color.RED);

        textPaint.setStrokeWidth(50);
        textPaint.setColor(Color.GREEN);
        textPaint.setStyle(Paint.Style.FILL);

        String imageUriStr = getIntent().getStringExtra("imageUri");
        if (imageUriStr != null) {
            Uri imageUri = Uri.parse(imageUriStr);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                bitmap = rotateBitmapIfRequired(this, imageUri, bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ArrayList<Recognition> recognitions = yolov5TFLiteDetector.detect(bitmap);
        ArrayList<String> resultList = new ArrayList<>();

        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas (mutableBitmap);



        Map<String, Boolean> mp = new HashMap<>();
        resultText = new StringBuilder();

        for(Recognition recognition: recognitions) {
            String recog = recognition.getLabelName();
            if(recognition.getConfidence() > 0.4) {
                RectF location = recognition.getLocation();
                canvas.drawRect(recognition.getLocation(), boxPaint);
                canvas.drawText(recog + ":" + recognition.getConfidence(), location.left, location.top, textPaint);

                if(!mp.containsKey(recog)) {
                    mp.put(recog, true);
                    resultList.add(recog);
                }
            }
        }

        if(resultList.isEmpty()) {
            resultText.append("ไม่พบผลลัพธ์");
        } else {
            resultText.append(String.join(", ", resultList));
            getAdviceFromGemini(resultList);
        }

        image = getImageUri(this, mutableBitmap);


        imageView.setImageURI(image);
        resultTextView.setText(resultText.toString());

    }

    private void getAdviceFromGemini(ArrayList<String> detectedLabels) {
        String prompt = "ฉันพบสิ่งเหล่านี้บนใบหน้าของฉัน: " + String.join(", ", detectedLabels) +
                " คุณช่วยให้คำแนะนำเกี่ยวกับวิธีดูแลผิวและรักษาสิวให้ฉันได้ไหม? กรุณาตอบกระชับ";

        GeminiRequest request = new GeminiRequest(
                Arrays.asList(new Content(Arrays.asList(new Part(prompt))))
        );

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

        RetrofitClient.getInstance().generateContent(url, request)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Candidate> candidates = response.body().getCandidates();
                            if(candidates != null && !candidates.isEmpty()) {
                                Content content = candidates.get(0).getContent();
                                if(content != null && content.getParts() != null && !content.getParts().isEmpty()) {
                                    advice = content.getParts().get(0).getText();

                                    if(markwon != null) markwon.setMarkdown(adviceTextView, advice);
                                    else adviceTextView.setText(advice);
                                    saveToDatabase();
                                    return;
                                }
                            }
                        } else {
                            adviceTextView.setText("Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeminiResponse> call, Throwable t) {
                        adviceTextView.setText("Request Failed: " + t.getMessage());
                    }
                });
    }

    private void saveToDatabase() {
        String username = getIntent().getStringExtra("USERNAME2");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if(username != null && !username.isEmpty()) {
            if(databaseHelper.insertHistory(username, resultText.toString(), date, image.toString(), advice)) Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "unsaved", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        File storageDir = new File(context.getExternalFilesDir(null), "Pictures");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
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


    private Bitmap rotateBitmapIfRequired(Context context, Uri uri, Bitmap bitmap) throws IOException {
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
