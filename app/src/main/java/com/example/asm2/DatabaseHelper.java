package com.example.asm2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asm2.AddDonationEditandDelete.DonationSite;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BloodDonation.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "donation_sites";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_HOURS = "hours";
    public static final String COLUMN_BLOOD_TYPES = "blood_types";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " TEXT NOT NULL, " +
                COLUMN_HOURS + " TEXT NOT NULL, " +
                COLUMN_BLOOD_TYPES + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new donation site
    public boolean insertDonationSite(String address, String hours, String bloodTypes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_HOURS, hours);
        values.put(COLUMN_BLOOD_TYPES, bloodTypes);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1; // Return true if the insert was successful
    }

    // Update an existing donation site by ID
    public boolean updateDonationSite(int id, String address, String hours, String bloodTypes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_HOURS, hours);
        values.put(COLUMN_BLOOD_TYPES, bloodTypes);

        int result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0; // Return true if the update was successful
    }

    // Delete a donation site by ID
    public boolean deleteDonationSite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0; // Return true if the delete was successful
    }

    // Retrieve all donation sites
    public List<DonationSite> getAllDonationSites() {
        List<DonationSite> donationSites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String hours = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOURS));
                String bloodTypes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_TYPES));
                donationSites.add(new DonationSite(id, address, hours, bloodTypes));
            }
            cursor.close();
        }
        return donationSites;
    }

    // Retrieve a single donation site by ID
    public DonationSite getDonationSiteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            String hours = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOURS));
            String bloodTypes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_TYPES));
            cursor.close();
            return new DonationSite(id, address, hours, bloodTypes);
        }
        return null; // Return null if the site is not found
    }

    // Clear all data in the table
    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}