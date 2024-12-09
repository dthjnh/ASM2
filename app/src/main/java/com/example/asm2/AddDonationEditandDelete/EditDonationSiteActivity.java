package com.example.asm2.AddDonationEditandDelete;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.DatabaseHelper;
import com.example.asm2.R;

public class EditDonationSiteActivity extends AppCompatActivity {

    private EditText editAddress, editHours, editBloodTypes;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int siteId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation_site);

        editAddress = findViewById(R.id.editAddress);
        editHours = findViewById(R.id.editHours);
        editBloodTypes = findViewById(R.id.editBloodTypes);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DatabaseHelper(this);

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
            editAddress.setText(site.getAddress());
            editHours.setText(site.getHours());
            editBloodTypes.setText(site.getBloodTypes());
        } else {
            Toast.makeText(this, "Donation site not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Save the updated data
        btnSave.setOnClickListener(v -> {
            String address = editAddress.getText().toString();
            String hours = editHours.getText().toString();
            String bloodTypes = editBloodTypes.getText().toString();

            if (address.isEmpty() || hours.isEmpty() || bloodTypes.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.updateDonationSite(siteId, address, hours, bloodTypes);
                if (success) {
                    Toast.makeText(this, "Donation site updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error updating site", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}