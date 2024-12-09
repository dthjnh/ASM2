package com.example.asm2.AddDonationEditandDelete;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.DatabaseHelper;
import com.example.asm2.R;

public class AddDonationSiteActivity extends AppCompatActivity {

    private EditText editAddress, editHours, editBloodTypes, editLatitude, editLongitude;
    private Button btnSave, btnListDonationSites;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation_site);

        editAddress = findViewById(R.id.editAddress);
        editHours = findViewById(R.id.editHours);
        editBloodTypes = findViewById(R.id.editBloodTypes);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        btnSave = findViewById(R.id.btnSave);
        btnListDonationSites = findViewById(R.id.btnListDonationSites);

        btnListDonationSites.setOnClickListener(v -> {
            startActivity(new Intent(AddDonationSiteActivity.this, DonationSiteListActivity.class));
        });

        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String address = editAddress.getText().toString().trim();
            String hours = editHours.getText().toString().trim();
            String bloodTypes = editBloodTypes.getText().toString().trim();
            String latitudeStr = editLatitude.getText().toString().trim();
            String longitudeStr = editLongitude.getText().toString().trim();

            // Validate inputs
            if (address.isEmpty() || hours.isEmpty() || bloodTypes.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                Toast.makeText(AddDonationSiteActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);

                // Insert the donation site into the database
                boolean success = dbHelper.insertDonationSite(address, hours, bloodTypes, latitude, longitude);
                if (success) {
                    Toast.makeText(AddDonationSiteActivity.this, "Donation site added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddDonationSiteActivity.this, DonationSiteListActivity.class));
                } else {
                    Toast.makeText(AddDonationSiteActivity.this, "Error adding site", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddDonationSiteActivity.this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
            }
        });
    }
}