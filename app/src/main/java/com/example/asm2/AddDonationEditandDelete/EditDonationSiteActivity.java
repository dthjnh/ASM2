package com.example.asm2.AddDonationEditandDelete;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Database.DonationSitesDatabaseHelper;
import com.example.asm2.R;

public class EditDonationSiteActivity extends AppCompatActivity {

    private EditText editName, editAddress, editHours, editBloodTypes, editLatitude, editLongitude,editDescription;
    private Button btnSave;
    private DonationSitesDatabaseHelper dbHelper;
    private int siteId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation_site);

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editHours = findViewById(R.id.editHours);
        editBloodTypes = findViewById(R.id.editBloodTypes);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        editDescription = findViewById(R.id.editDescription);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DonationSitesDatabaseHelper(this);

        // Get the site ID passed from the adapter
        siteId = getIntent().getIntExtra("site_id", -1);

        if (siteId == -1) {
            Toast.makeText(this, "Invalid site ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load site data
        DonationSite site = dbHelper.getDonationSiteById(siteId);
        if (site != null) {
            editName.setText(site.getName());
            editAddress.setText(site.getAddress());
            editHours.setText(site.getHours());
            editBloodTypes.setText(site.getBloodTypes());
            editLatitude.setText(String.valueOf(site.getLatitude()));
            editLongitude.setText(String.valueOf(site.getLongitude()));
            editDescription.setText(site.getCreatorType());
        } else {
            Toast.makeText(this, "Donation site not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Save the updated data
        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String hours = editHours.getText().toString().trim();
            String bloodTypes = editBloodTypes.getText().toString().trim();
            String latitudeStr = editLatitude.getText().toString().trim();
            String longitudeStr = editLongitude.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || hours.isEmpty() || bloodTypes.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);

                // Update the donation site in the database
                boolean success = dbHelper.updateDonationSite(siteId, name, address, hours, bloodTypes, latitude, longitude, description);
                if (success) {
                    Toast.makeText(this, "Donation site updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error updating site", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
            }
        });
    }
}