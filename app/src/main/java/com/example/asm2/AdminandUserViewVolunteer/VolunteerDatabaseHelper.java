package com.example.asm2.AdminandUserViewVolunteer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VolunteerDatabaseHelper extends SQLiteOpenHelper {

    // Database constants
    private static final String DATABASE_NAME = "VolunteerDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Volunteers table
    public static final String TABLE_VOLUNTEERS = "volunteers";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";

    public VolunteerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create volunteers table
        String createTable = "CREATE TABLE " + TABLE_VOLUNTEERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PHONE + " TEXT NOT NULL);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOLUNTEERS);
        onCreate(db);
    }

    // Insert a new volunteer
    public boolean insertVolunteer(String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);

        long result = db.insert(TABLE_VOLUNTEERS, null, values);
        db.close();
        return result != -1;
    }

    // Update an existing volunteer
    public boolean updateVolunteer(int id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);

        int rowsUpdated = db.update(TABLE_VOLUNTEERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsUpdated > 0;
    }

    // Delete a volunteer
    public boolean deleteVolunteer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_VOLUNTEERS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    // Retrieve all volunteers
    public Cursor getAllVolunteers() {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery("SELECT id AS _id, name, phone FROM " + TABLE_VOLUNTEERS, null);
}

    // Retrieve a volunteer by ID
    public Cursor getVolunteerById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VOLUNTEERS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }
}
