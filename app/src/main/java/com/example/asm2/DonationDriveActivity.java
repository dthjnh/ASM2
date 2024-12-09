package com.example.asm2;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DonationDriveActivity extends AppCompatActivity {

    private Spinner donorNameSpinner;
    private EditText editBloodAmount, editBloodTypes;
    private Button btnSubmitDonation;
    private TextView donationDetailsText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_drive);

        dbHelper = new DatabaseHelper(this);

        donorNameSpinner = findViewById(R.id.donorNameSpinner);
        editBloodAmount = findViewById(R.id.editBloodAmount);
        editBloodTypes = findViewById(R.id.editBloodTypes);
        btnSubmitDonation = findViewById(R.id.btnSubmitDonation);
        donationDetailsText = findViewById(R.id.donationDetailsText);

        // Load donor names from the database
        loadDonors();

        // Set button click listener for submitting donation details
        btnSubmitDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDonationData();
            }
        });
    }

    // Load donor names from the database and display them in the spinner
    private void loadDonors() {
        Cursor cursor = dbHelper.getAllDonors();
        if (cursor != null && cursor.moveToFirst()) {
            List<String> donorNames = new ArrayList<>();
            do {
                @SuppressLint("Range") String donorName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DONOR_COLUMN_NAME));
                donorNames.add(donorName);
            } while (cursor.moveToNext());
            cursor.close();

            // Set up the spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, donorNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            donorNameSpinner.setAdapter(adapter);
        }
    }

    // Submit donation data to the database
    @SuppressLint("Range")
    private void submitDonationData() {
        String selectedDonorName = donorNameSpinner.getSelectedItem().toString();
        String bloodAmount = editBloodAmount.getText().toString();
        String bloodTypes = editBloodTypes.getText().toString();

        // Find donor ID from the name (you could optimize this further with a map)
        Cursor cursor = dbHelper.getAllDonors();
        int donorId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String donorName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DONOR_COLUMN_NAME));
                if (donorName.equals(selectedDonorName)) {
                    donorId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DONOR_COLUMN_ID));
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Check for empty fields
        if (bloodAmount.isEmpty() || bloodTypes.isEmpty()) {
            Toast.makeText(DonationDriveActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the donation data into the database
        if (donorId != -1) {
            boolean isInserted = dbHelper.insertDonationDrive(donorId, bloodAmount, bloodTypes);
            if (isInserted) {
                Toast.makeText(DonationDriveActivity.this, "Donation data submitted", Toast.LENGTH_SHORT).show();
                displayDonationData(donorId); // Display the donation data below the donor's name
            } else {
                Toast.makeText(DonationDriveActivity.this, "Failed to submit donation data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Display the donation details below the donor's name
    private void displayDonationData(int donorId) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.DONATION_DRIVE_TABLE + " WHERE " + DatabaseHelper.COLUMN_DONOR_ID + " = ?",
                new String[]{String.valueOf(donorId)});

        StringBuilder details = new StringBuilder();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String bloodAmount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BLOOD_AMOUNT));
                @SuppressLint("Range") String bloodTypes = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BLOOD_TYPES));
                details.append("Amount: ").append(bloodAmount).append("\n");
                details.append("Blood Types: ").append(bloodTypes).append("\n\n");
            } while (cursor.moveToNext());
            cursor.close();
        }

        donationDetailsText.setText(details.toString());
    }
}
