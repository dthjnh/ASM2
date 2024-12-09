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
    private static final int DATABASE_VERSION = 3; // Incremented for donor table

    // Donation sites table
    public static final String TABLE_NAME = "donation_sites";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_HOURS = "hours";
    public static final String COLUMN_BLOOD_TYPES = "blood_types";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // Donors table
    public static final String DONORS_TABLE_NAME = "donors";
    public static final String DONOR_COLUMN_ID = "id";
    public static final String DONOR_COLUMN_NAME = "name";
    public static final String DONOR_COLUMN_CONTACT = "contact";
    public static final String DONOR_COLUMN_SITE_ADDRESS = "site_address";

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
                COLUMN_LONGITUDE + " REAL)";
        db.execSQL(createDonationSitesTable);

        // Create donors table
        String createDonorsTable = "CREATE TABLE " + DONORS_TABLE_NAME + " (" +
                DONOR_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DONOR_COLUMN_NAME + " TEXT NOT NULL, " +
                DONOR_COLUMN_CONTACT + " TEXT NOT NULL, " +
                DONOR_COLUMN_SITE_ADDRESS + " TEXT NOT NULL)";
        db.execSQL(createDonorsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LATITUDE + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LONGITUDE + " REAL");
        }
        if (oldVersion < 3) {
            String createDonorsTable = "CREATE TABLE " + DONORS_TABLE_NAME + " (" +
                    DONOR_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DONOR_COLUMN_NAME + " TEXT NOT NULL, " +
                    DONOR_COLUMN_CONTACT + " TEXT NOT NULL, " +
                    DONOR_COLUMN_SITE_ADDRESS + " TEXT NOT NULL)";
            db.execSQL(createDonorsTable);
        }
    }

    // Donation Sites Methods

    public boolean insertDonationSite(String address, String hours, String bloodTypes, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_HOURS, hours);
        values.put(COLUMN_BLOOD_TYPES, bloodTypes);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean updateDonationSite(int id, String address, String hours, String bloodTypes, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_HOURS, hours);
        values.put(COLUMN_BLOOD_TYPES, bloodTypes);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);

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
                donationSites.add(new DonationSite(id, address, hours, bloodTypes, latitude, longitude));
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
            cursor.close();
            return new DonationSite(id, address, hours, bloodTypes, latitude, longitude);
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

    public Cursor getAllDonors() {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery("SELECT id AS _id, name, contact, site_address FROM " + DONORS_TABLE_NAME, null);
}
}