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

    // Database constants
    private static final String DATABASE_NAME = "BloodDonation.db";
    private static final int DATABASE_VERSION = 6;

    // Donation sites table
    public static final String TABLE_NAME = "donation_sites";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_HOURS = "hours";
    public static final String COLUMN_BLOOD_TYPES = "blood_types";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_CREATOR_TYPE = "creator_type";

    // Donors table
    public static final String DONORS_TABLE_NAME = "donors";
    public static final String DONOR_COLUMN_ID = "id";
    public static final String DONOR_COLUMN_NAME = "name";
    public static final String DONOR_COLUMN_CONTACT = "contact";
    public static final String DONOR_COLUMN_SITE_ADDRESS = "site_address";

    // Donation drive table
    public static final String DONATION_DRIVE_TABLE = "donation_drive";
    public static final String COLUMN_DONOR_ID = "donor_id";
    public static final String COLUMN_BLOOD_AMOUNT = "blood_amount";
    public static final String COLUMN_DONATION_BLOOD_TYPES = "donation_blood_types";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create donation sites table
        String createDonationSitesTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " TEXT NOT NULL, " +
                COLUMN_HOURS + " TEXT NOT NULL, " +
                COLUMN_BLOOD_TYPES + " TEXT NOT NULL, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_CREATOR_TYPE + " TEXT NOT NULL)";
        db.execSQL(createDonationSitesTable);

        // Create donors table
        String createDonorsTable = "CREATE TABLE " + DONORS_TABLE_NAME + " (" +
                DONOR_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DONOR_COLUMN_NAME + " TEXT NOT NULL, " +
                DONOR_COLUMN_CONTACT + " TEXT NOT NULL, " +
                DONOR_COLUMN_SITE_ADDRESS + " TEXT NOT NULL)";
        db.execSQL(createDonorsTable);

        // Create donation drive table
        String createDonationDriveTable = "CREATE TABLE " + DONATION_DRIVE_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DONOR_ID + " INTEGER NOT NULL, " +
                COLUMN_BLOOD_AMOUNT + " TEXT NOT NULL, " +
                COLUMN_DONATION_BLOOD_TYPES + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_DONOR_ID + ") REFERENCES " + DONORS_TABLE_NAME + "(" + DONOR_COLUMN_ID + "))";
        db.execSQL(createDonationDriveTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LATITUDE + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LONGITUDE + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_CREATOR_TYPE + " TEXT;");
        }
        if (oldVersion < 3) {
            String createDonorsTable = "CREATE TABLE " + DONORS_TABLE_NAME + " (" +
                    DONOR_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DONOR_COLUMN_NAME + " TEXT NOT NULL, " +
                    DONOR_COLUMN_CONTACT + " TEXT NOT NULL, " +
                    DONOR_COLUMN_SITE_ADDRESS + " TEXT NOT NULL)";
            db.execSQL(createDonorsTable);
        }
        if (oldVersion < 4) {
            String createDonationDriveTable = "CREATE TABLE " + DONATION_DRIVE_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DONOR_ID + " INTEGER NOT NULL, " +
                    COLUMN_BLOOD_AMOUNT + " TEXT NOT NULL, " +
                    COLUMN_DONATION_BLOOD_TYPES + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_DONOR_ID + ") REFERENCES " + DONORS_TABLE_NAME + "(" + DONOR_COLUMN_ID + "))";
            db.execSQL(createDonationDriveTable);
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_CREATOR_TYPE + " TEXT NOT NULL DEFAULT 'admin'");
        }
    }

    // Donation Sites Methods

    public boolean insertDonationSite(String address, String hours, String bloodTypes, double latitude, double longitude, String creatorType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_HOURS, hours);
        values.put(COLUMN_BLOOD_TYPES, bloodTypes);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_CREATOR_TYPE, creatorType);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean updateDonationSite(int id, String address, String hours, String bloodTypes, double latitude, double longitude, String creatorType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_HOURS, hours);
        values.put(COLUMN_BLOOD_TYPES, bloodTypes);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_CREATOR_TYPE, creatorType);

        int result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteDonationSite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

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
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
                String creatorType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR_TYPE));
                donationSites.add(new DonationSite(id, address, hours, bloodTypes, latitude, longitude, creatorType));
            }
            cursor.close();
        }
        return donationSites;
    }

    public DonationSite getDonationSiteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            String hours = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOURS));
            String bloodTypes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_TYPES));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
            String creatorType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR_TYPE));
            cursor.close();
            return new DonationSite(id, address, hours, bloodTypes, latitude, longitude, creatorType);
        }
        return null;
    }

    // Donors Methods

    public boolean registerDonor(String name, String contact, String siteAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DONOR_COLUMN_NAME, name);
        values.put(DONOR_COLUMN_CONTACT, contact);
        values.put(DONOR_COLUMN_SITE_ADDRESS, siteAddress);

        long result = db.insert(DONORS_TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean updateDonor(int donorId, String name, String contact, String siteAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DONOR_COLUMN_NAME, name);
        values.put(DONOR_COLUMN_CONTACT, contact);
        values.put(DONOR_COLUMN_SITE_ADDRESS, siteAddress);

        int rowsAffected = db.update(DONORS_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(donorId)});
        return rowsAffected > 0; // Return true if update was successful
    }

    public Cursor getAllDonors() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, name, contact, site_address FROM " + DONORS_TABLE_NAME, null);
    }

    public Cursor getDonorById(int donorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + DONORS_TABLE_NAME + " WHERE id = ?",
                new String[]{String.valueOf(donorId)}
        );
    }

    public boolean deleteDonor(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(DONORS_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0; // Return true if the donor was successfully deleted
    }

    // Donation Drive Methods

    public boolean insertDonationDrive(int donorId, String bloodAmount, String bloodTypes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DONOR_ID, donorId);
        values.put(COLUMN_BLOOD_AMOUNT, bloodAmount);
        values.put(COLUMN_DONATION_BLOOD_TYPES, bloodTypes);

        long result = db.insert(DONATION_DRIVE_TABLE, null, values);
        return result != -1;
    }

    public Cursor getDonationData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, donor_name, blood_amount, blood_types FROM donations", null);
    }

    // Method to delete a donation record by ID
    public boolean deleteDonationRecord(int donorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(DONATION_DRIVE_TABLE, COLUMN_DONOR_ID + "=?", new String[]{String.valueOf(donorId)});
        return rowsDeleted > 0; // Return true if deletion was successful
    }
}