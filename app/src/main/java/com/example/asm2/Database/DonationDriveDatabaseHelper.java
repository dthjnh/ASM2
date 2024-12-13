package com.example.asm2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DonationDriveDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DonationDrive.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "donation_drive";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DONOR_ID = "donor_id";
    public static final String COLUMN_BLOOD_AMOUNT = "blood_amount";
    public static final String COLUMN_BLOOD_TYPES = "blood_types";

    public DonationDriveDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DONOR_ID + " INTEGER NOT NULL, " +
                COLUMN_BLOOD_AMOUNT + " TEXT NOT NULL, " +
                COLUMN_BLOOD_TYPES + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_DONOR_ID + ") REFERENCES donors(id))";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertDonationDrive(int donorId, String bloodAmount, String bloodTypes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DONOR_ID, donorId);
        values.put(COLUMN_BLOOD_AMOUNT, bloodAmount);
        values.put(COLUMN_BLOOD_TYPES, bloodTypes);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public Cursor getDonationData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_ID + " AS _id, " + COLUMN_DONOR_ID + ", " + COLUMN_BLOOD_AMOUNT + ", " + COLUMN_BLOOD_TYPES + " FROM " + TABLE_NAME, null);
    }

    public boolean deleteDonationRecord(int donorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_DONOR_ID + "=?", new String[]{String.valueOf(donorId)});
        return rowsDeleted > 0;
    }

}