package com.example.asm2.AddDonationSiteUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.AddDonationEditandDelete.AddDonationSiteActivity;
import com.example.asm2.AddDonationEditandDelete.DonationSiteListActivity;
import com.example.asm2.Database.DonationSitesDatabaseHelper;
import com.example.asm2.MapView.MapsActivity;
import com.example.asm2.R;

public class AddDonationSiteUserActivity extends AppCompatActivity {

    private EditText editName, editAddress, editHours, editBloodTypes, editLatitude, editLongitude;
    private Button btnSave, btnViewOnMap, btnListDonationSites;
    private DonationSitesDatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation_site_user);

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editHours = findViewById(R.id.editHours);
        editBloodTypes = findViewById(R.id.editBloodTypes);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        btnSave = findViewById(R.id.btnSave);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);
        btnListDonationSites = findViewById(R.id.btnListDonationSites);

        dbHelper = new DonationSitesDatabaseHelper(this);

        btnViewOnMap.setOnClickListener(v -> {
            startActivity(new Intent(AddDonationSiteUserActivity.this, MapsActivity.class));
        });

        btnListDonationSites.setOnClickListener(v -> {
            startActivity(new Intent(AddDonationSiteUserActivity.this, DonationSiteListActivity.class));
        });

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String hours = editHours.getText().toString().trim();
            String bloodTypes = editBloodTypes.getText().toString().trim();
            String latitudeStr = editLatitude.getText().toString().trim();
            String longitudeStr = editLongitude.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || hours.isEmpty() || bloodTypes.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                Toast.makeText(AddDonationSiteUserActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);

                boolean success = dbHelper.insertDonationSite(name, address, hours, bloodTypes, latitude, longitude, "user");
                if (success) {
                    Toast.makeText(AddDonationSiteUserActivity.this, "Donation site added successfully", Toast.LENGTH_SHORT).show();
                    editName.setText("");
                    editAddress.setText("");
                    editHours.setText("");
                    editBloodTypes.setText("");
                    editLatitude.setText("");
                    editLongitude.setText("");
                    startActivity(new Intent(AddDonationSiteUserActivity.this, DonationSiteListActivity.class));
                } else {
                    Toast.makeText(AddDonationSiteUserActivity.this, "Error adding site", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddDonationSiteUserActivity.this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
            }
        });
    }
}