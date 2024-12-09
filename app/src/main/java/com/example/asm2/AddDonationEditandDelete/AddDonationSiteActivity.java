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

    private EditText editAddress, editHours, editBloodTypes;
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
        btnSave = findViewById(R.id.btnSave);
        btnListDonationSites = findViewById(R.id.btnListDonationSites);

        btnListDonationSites.setOnClickListener(v -> {
            startActivity(new Intent(AddDonationSiteActivity.this, DonationSiteListActivity.class));
        });

        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String address = editAddress.getText().toString();
            String hours = editHours.getText().toString();
            String bloodTypes = editBloodTypes.getText().toString();

            if (address.isEmpty() || hours.isEmpty() || bloodTypes.isEmpty()) {
                Toast.makeText(AddDonationSiteActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.insertDonationSite(address, hours, bloodTypes);
                if (success) {
                    Toast.makeText(AddDonationSiteActivity.this, "Donation site added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddDonationSiteActivity.this, DonationSiteListActivity.class));
                } else {
                    Toast.makeText(AddDonationSiteActivity.this, "Error adding site", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}