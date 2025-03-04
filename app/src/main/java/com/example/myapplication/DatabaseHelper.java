package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Login.db";
    private static final String COL1 = "ID";
    private static final String COL2 = "USERNAME";
    private static final String COL3 = "PASSWORD";
    private static final String COL4 = "FIRSTNAME";
    private static final String COL5 = "SURNAME";
    private static final String COL6 = "AGE";
    private static final String COL7 = "WEIGHT";
    private static final String COL8 = "HEIGHT";
    private static final String COL9 = "PROFILE_PIC";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME TEXT, PASSWORD TEXT, " +
                "FIRSTNAME TEXT, SURNAME TEXT, AGE INTEGER, " +
                "WEIGHT REAL, HEIGHT REAL, PROFILE_PIC BLOB)");

        db.execSQL("CREATE TABLE History(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USERNAME TEXT, RESULT TEXT, DATE TEXT, PIC TEXT, ADVICE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    public boolean insertData (String username, String password, String firstname, String surname, Integer age, Double weight, Double height) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, username);
        contentValues.put(COL3, password);
        contentValues.put(COL4, firstname);
        contentValues.put(COL5, surname);
        contentValues.put(COL6, age);
        contentValues.put(COL7, weight);
        contentValues.put(COL8, height);

        long result = db.insert("Users", null, contentValues);
        return result != -1;
    }

    @SuppressLint("Range")
    public String checkLogin(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {COL2};
        String selection = "USERNAME=? AND PASSWORD =  ?";
        String[] selectionArgs = {username, password};
        String result = null;
        Cursor cursor = db.query("Users", columns, selection, selectionArgs, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(COL2));
            cursor.close();
        }
        return result;
    }

    public Cursor getuserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Users WHERE USERNAME = ?", new String[]{username});
    }

    public boolean updateData(String username, String firstname, String surname, int age, double weight, double height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL4, firstname);
        contentValues.put(COL5, surname);
        contentValues.put(COL6, age);
        contentValues.put(COL7, weight);
        contentValues.put(COL8, height);

        int result = db.update("Users", contentValues, "USERNAME = ?", new String[]{username});
        return result > 0;
    }

    public boolean insertHistory (String username, String result, String date, String image, String advice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USERNAME", username);
        contentValues.put("RESULT", result);
        contentValues.put("DATE", date);
        contentValues.put("PIC", image);
        contentValues.put("ADVICE", advice);

        long res = db.insert("History", null, contentValues);
        return res != -1;
    }

    public Cursor getHistory(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT ID AS _id, USERNAME, RESULT, DATE, PIC, ADVICE FROM History WHERE USERNAME = ? ORDER BY DATE DESC", new String[]{username});
    }


}
