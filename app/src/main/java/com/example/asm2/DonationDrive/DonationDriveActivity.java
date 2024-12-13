package com.example.asm2.DonationDrive;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Database.DonationDriveDatabaseHelper;
import com.example.asm2.Database.DonorsDatabaseHelper;
import com.example.asm2.R;

import java.util.ArrayList;
import java.util.List;

public class DonationDriveActivity extends AppCompatActivity {

    private Spinner donorNameSpinner;
    private EditText editBloodAmount, editBloodTypes;
    private Button btnSubmitDonation;
    private TextView donationDetailsText;
    private DonationDriveDatabaseHelper donationDriveDbHelper;
    private DonorsDatabaseHelper donorsDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_drive);

        donationDriveDbHelper = new DonationDriveDatabaseHelper(this);
        donorsDbHelper = new DonorsDatabaseHelper(this);

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
        Cursor cursor = donorsDbHelper.getAllDonors();
        if (cursor != null && cursor.moveToFirst()) {
            List<String> donorNames = new ArrayList<>();
            do {
                @SuppressLint("Range") String donorName = cursor.getString(cursor.getColumnIndex(DonorsDatabaseHelper.COLUMN_NAME));
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

        if (bloodAmount.isEmpty() || bloodTypes.isEmpty()) {
            Toast.makeText(DonationDriveActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = donorsDbHelper.getAllDonors();
        int donorId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String donorName = cursor.getString(cursor.getColumnIndex(DonorsDatabaseHelper.COLUMN_NAME));
                if (donorName.equals(selectedDonorName)) {
                    donorId = cursor.getInt(cursor.getColumnIndex("_id"));
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (donorId == -1) {
            Toast.makeText(DonationDriveActivity.this, "Donor not found", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = donationDriveDbHelper.insertDonationDrive(donorId, bloodAmount, bloodTypes);
        if (isInserted) {
            Toast.makeText(DonationDriveActivity.this, "Donation data submitted", Toast.LENGTH_SHORT).show();
            editBloodAmount.setText("");
            editBloodTypes.setText("");
            displayDonationData(donorId);
        } else {
            Toast.makeText(DonationDriveActivity.this, "Failed to submit donation data", Toast.LENGTH_SHORT).show();
        }
    }

    // Display the donation details below the donor's name
    private void displayDonationData(int donorId) {
        // Fetch donor details (name, contact, and site address)
        String donorName = "";
        String donorContact = "";
        String donorSiteAddress = "";

        Cursor donorCursor = donorsDbHelper.getReadableDatabase().rawQuery(
                "SELECT name, contact, site_address FROM " + DonorsDatabaseHelper.TABLE_NAME +
                        " WHERE " + DonorsDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(donorId)}
        );

        if (donorCursor != null && donorCursor.moveToFirst()) {
            int nameIndex = donorCursor.getColumnIndex("name");
            int contactIndex = donorCursor.getColumnIndex("contact");
            int siteAddressIndex = donorCursor.getColumnIndex("site_address");

            if (nameIndex != -1) {
                donorName = donorCursor.getString(nameIndex);
            }
            if (contactIndex != -1) {
                donorContact = donorCursor.getString(contactIndex);
            }
            if (siteAddressIndex != -1) {
                donorSiteAddress = donorCursor.getString(siteAddressIndex);
            }
            donorCursor.close();
        }

        // Fetch donation data for the selected donor
        Cursor cursor = donationDriveDbHelper.getReadableDatabase().rawQuery(
                "SELECT id, " + DonationDriveDatabaseHelper.COLUMN_BLOOD_AMOUNT + ", " + DonationDriveDatabaseHelper.COLUMN_BLOOD_TYPES +
                        " FROM " + DonationDriveDatabaseHelper.TABLE_NAME +
                        " WHERE " + DonationDriveDatabaseHelper.COLUMN_DONOR_ID + " = ? ORDER BY id DESC",
                new String[]{String.valueOf(donorId)}
        );

        StringBuilder details = new StringBuilder();
        boolean hasData = false; // Track if any data is available

        // Add donor details to the top
        details.append("Donor Details:\n");
        details.append("Name: ").append(donorName).append("\n");
        details.append("Contact: ").append(donorContact).append("\n");
        details.append("Site Address: ").append(donorSiteAddress).append("\n\n");

        if (cursor != null && cursor.moveToFirst()) {
            hasData = true; // Data is available
            details.append("Recent Donations:\n\n");

            do {
                int recordIdIndex = cursor.getColumnIndex("id");
                int bloodAmountIndex = cursor.getColumnIndex(DonationDriveDatabaseHelper.COLUMN_BLOOD_AMOUNT);
                int bloodTypesIndex = cursor.getColumnIndex(DonationDriveDatabaseHelper.COLUMN_BLOOD_TYPES);

                // Ensure column indexes are valid
                if (recordIdIndex != -1 && bloodAmountIndex != -1 && bloodTypesIndex != -1) {
                    int recordId = cursor.getInt(recordIdIndex);
                    String bloodAmount = cursor.getString(bloodAmountIndex);
                    String bloodTypes = cursor.getString(bloodTypesIndex);

                    // Append data to details
                    details.append("Record ID: ").append(recordId).append("\n");
                    details.append("Amount: ").append(bloodAmount).append("\n");
                    details.append("Blood Types: ").append(bloodTypes).append("\n\n");
                } else {
                    Log.e("DatabaseError", "Column not found in Cursor");
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Display data or show a default message if empty
        if (!hasData) {
            details.append("No donation data available for this donor.");
        }
        donationDetailsText.setText(details.toString());

        // Clear previous inputs
        editBloodAmount.setText("");
        editBloodTypes.setText("");

        // Set up click-to-delete functionality for donation records
        if (hasData) {
            donationDetailsText.setOnClickListener(v -> {
                // Display an action or dialog for selecting which record to delete
                showDeleteDialog(donorId); // Implement this method to handle record-specific deletion
            });
        } else {
            donationDetailsText.setOnClickListener(null); // Clear click listener when no data exists
        }
    }

    // Method to delete all donation data for a specific donor
    private void deleteDonationData(int recordId, int donorId) {
        int rowsDeleted = donationDriveDbHelper.getWritableDatabase().delete(
                DonationDriveDatabaseHelper.TABLE_NAME,
                "id = ?",
                new String[]{String.valueOf(recordId)}
        );

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Record deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete record.", Toast.LENGTH_SHORT).show();
        }

        // Refresh the displayed data after deletion
        displayDonationData(donorId);
    }

    private void showDeleteDialog(int donorId) {
        // Fetch donation records for the selected donor
        Cursor cursor = donationDriveDbHelper.getReadableDatabase().rawQuery(
                "SELECT id, " + DonationDriveDatabaseHelper.COLUMN_BLOOD_AMOUNT + ", " + DonationDriveDatabaseHelper.COLUMN_BLOOD_TYPES +
                        " FROM " + DonationDriveDatabaseHelper.TABLE_NAME +
                        " WHERE " + DonationDriveDatabaseHelper.COLUMN_DONOR_ID + " = ?",
                new String[]{String.valueOf(donorId)}
        );

        // Prepare a list of records for the dialog
        List<String> records = new ArrayList<>();
        List<Integer> recordIds = new ArrayList<>(); // To store record IDs

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int recordId = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String bloodAmount = cursor.getString(cursor.getColumnIndex(DonationDriveDatabaseHelper.COLUMN_BLOOD_AMOUNT));
                @SuppressLint("Range") String bloodTypes = cursor.getString(cursor.getColumnIndex(DonationDriveDatabaseHelper.COLUMN_BLOOD_TYPES));

                records.add("Amount: " + bloodAmount + ", Blood Types: " + bloodTypes);
                recordIds.add(recordId);
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (records.isEmpty()) {
            Toast.makeText(this, "No donation records to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show an AlertDialog with donation records
        new AlertDialog.Builder(this)
                .setTitle("Select a record to delete")
                .setItems(records.toArray(new String[0]), (dialog, which) -> {
                    // 'which' is the index of the selected record in the list
                    int selectedRecordId = recordIds.get(which);

                    // Confirm deletion
                    confirmDelete(selectedRecordId, donorId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(int recordId, int donorId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteDonationData(recordId, donorId);
                })
                .setNegativeButton("No", null)
                .show();
    }
}