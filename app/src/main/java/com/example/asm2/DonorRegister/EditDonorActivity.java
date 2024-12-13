package com.example.asm2.DonorRegister;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Database.DonorsDatabaseHelper;
import com.example.asm2.R;

public class EditDonorActivity extends AppCompatActivity {

    private EditText editName, editContact, editSiteAddress;
    private Button btnSave;
    private DonorsDatabaseHelper dbHelper;
    private int donorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donor);

        editName = findViewById(R.id.editName);
        editContact = findViewById(R.id.editContact);
        editSiteAddress = findViewById(R.id.editSiteAddress);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DonorsDatabaseHelper(this);

        // Get donor ID from the intent
        donorId = getIntent().getIntExtra("donor_id", -1);

        if (donorId != -1) {
            loadDonorDetails(donorId);
        }

        btnSave.setOnClickListener(v -> saveDonorDetails());
    }

    @SuppressLint("Range")
    private void loadDonorDetails(int donorId) {
        Cursor cursor = dbHelper.getDonorById(donorId);

        if (cursor != null && cursor.moveToFirst()) {
            editName.setText(cursor.getString(cursor.getColumnIndex(DonorsDatabaseHelper.COLUMN_NAME)));
            editContact.setText(cursor.getString(cursor.getColumnIndex(DonorsDatabaseHelper.COLUMN_CONTACT)));
            editSiteAddress.setText(cursor.getString(cursor.getColumnIndex(DonorsDatabaseHelper.COLUMN_SITE_ADDRESS)));
            cursor.close();
        } else {
            Toast.makeText(this, "Failed to load donor details", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if donor details can't be loaded
        }
    }

    private void saveDonorDetails() {
        String name = editName.getText().toString().trim();
        String contact = editContact.getText().toString().trim();
        String siteAddress = editSiteAddress.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty() || siteAddress.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateDonor(donorId, name, contact, siteAddress);
        if (success) {
            Toast.makeText(this, "Donor details updated successfully", Toast.LENGTH_SHORT).show();
            editName.setText("");
            editContact.setText("");
            editSiteAddress.setText("");
            finish(); // Close the activity after saving
        } else {
            Toast.makeText(this, "Failed to update donor details", Toast.LENGTH_SHORT).show();
        }
    }
}