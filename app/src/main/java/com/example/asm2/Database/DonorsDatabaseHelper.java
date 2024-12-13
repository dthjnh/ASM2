package com.example.asm2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DonorsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Donors.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "donors";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_SITE_ADDRESS = "site_address";

    public DonorsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_CONTACT + " TEXT NOT NULL, " +
                COLUMN_SITE_ADDRESS + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean registerDonor(String name, String contact, String siteAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CONTACT, contact);
        values.put(COLUMN_SITE_ADDRESS, siteAddress);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean updateDonor(int donorId, String name, String contact, String siteAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CONTACT, contact);
        values.put(COLUMN_SITE_ADDRESS, siteAddress);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(donorId)});
        if (rowsAffected > 0){
            return true;
        } else {
            Log.e("DonorsDatabaseHelper", "Failed to update donor with ID: " + donorId);
            return false;
        }
    }

    public Cursor getAllDonors() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_ID + " AS _id, " + COLUMN_NAME + ", " + COLUMN_CONTACT + ", " + COLUMN_SITE_ADDRESS + " FROM " + TABLE_NAME, null);
    }

    public Cursor getDonorById(int donorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(donorId)});
    }

    public boolean deleteDonor(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0;
    }
}