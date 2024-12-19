package com.example.asm2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.AddDonationEditandDelete.DonationSite;
import com.example.asm2.Database.DonationDriveDatabaseHelper;
import com.example.asm2.Database.DonationSitesDatabaseHelper;
import com.example.asm2.Database.DonorsDatabaseHelper;
import com.example.asm2.Login.SignIn;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperUserActivity extends AppCompatActivity {

    private DonationDriveDatabaseHelper dbHelper;
    private DonationSitesDatabaseHelper donationSitesDatabaseHelper;
    private DonorsDatabaseHelper donorsDatabaseHelper;
    private TextView reportTextView;
    private Button generateReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);

        dbHelper = new DonationDriveDatabaseHelper(this);
        donationSitesDatabaseHelper = new DonationSitesDatabaseHelper(this);
        donorsDatabaseHelper = new DonorsDatabaseHelper(this);
        reportTextView = findViewById(R.id.reportTextView);
        generateReportButton = findViewById(R.id.generateReportButton);

        generateReportButton.setOnClickListener(v -> generateReport());
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();

        // Fetch data from the donation sites table
        List<DonationSite> donationSites = donationSitesDatabaseHelper.getAllDonationSites();
        if (!donationSites.isEmpty()) {
            int totalSites = donationSites.size();
            StringBuilder sitesList = new StringBuilder();

            for (DonationSite site : donationSites) {
                sitesList.append("\nAddress: ").append(site.getAddress()).append("\n")
                         .append("Opening Hours: ").append(site.getHours()).append("\n")
                         .append("Blood Types Required: ").append(site.getBloodTypes()).append("\n")
                         .append("Latitude: ").append(site.getLatitude()).append("\n")
                         .append("Longitude: ").append(site.getLongitude()).append("\n")
                         .append("Description: ").append(site.getCreatorType()).append("\n");
            }

            // Append donation sites summary
            report.append("\nDonation Sites:\n")
                    .append(sitesList.toString())
                    .append("\nTotal Donation Sites: ").append(totalSites).append("\n");
        } else {
            report.append("\nNo donation sites available.");
        }

        // Fetch data from the donations table
        Cursor cursor = dbHelper.getDonationData();
        if (cursor != null && cursor.getCount() > 0) {
            int totalDonors = 0;
            double totalBloodVolume = 0;
            double bloodTypeA = 0;
            double bloodTypeB = 0;
            double bloodTypeAB = 0;
            double bloodTypeO = 0;
            StringBuilder bloodTypesCollected = new StringBuilder();
            StringBuilder donorInfo = new StringBuilder();
            Set<Integer> uniqueDonorIds = new HashSet<>();

            while (cursor.moveToNext()) {
                double bloodVolume = cursor.getDouble(cursor.getColumnIndexOrThrow(DonationDriveDatabaseHelper.COLUMN_BLOOD_AMOUNT));
                String bloodType = cursor.getString(cursor.getColumnIndexOrThrow(DonationDriveDatabaseHelper.COLUMN_BLOOD_TYPES));
                int donorId = cursor.getInt(cursor.getColumnIndexOrThrow(DonationDriveDatabaseHelper.COLUMN_DONOR_ID));

                totalBloodVolume += bloodVolume;

                switch (bloodType) {
                    case "A":
                        bloodTypeA += bloodVolume;
                        break;
                    case "B":
                        bloodTypeB += bloodVolume;
                        break;
                    case "AB":
                        bloodTypeAB += bloodVolume;
                        break;
                    case "O":
                        bloodTypeO += bloodVolume;
                        break;
                }

                if (!bloodTypesCollected.toString().contains(bloodType)) {
                    bloodTypesCollected.append(bloodType).append(", ");
                }

                // Check if donor ID is unique
                if (uniqueDonorIds.add(donorId)) {
                    totalDonors++;

                    // Fetch donor information
                    Cursor donorCursor = donorsDatabaseHelper.getDonorById(donorId);
                    if (donorCursor != null && donorCursor.moveToFirst()) {
                        String donorName = donorCursor.getString(donorCursor.getColumnIndexOrThrow(DonorsDatabaseHelper.COLUMN_NAME));
                        String donorContact = donorCursor.getString(donorCursor.getColumnIndexOrThrow(DonorsDatabaseHelper.COLUMN_CONTACT));

                        // Append donor information
                        donorInfo.append("\nDonor Name: ").append(donorName).append("\n")
                                 .append("Contact: ").append(donorContact).append("\n");
                        donorCursor.close();
                    }
                }
            }
            cursor.close();

            // Generate report summary
            report.append("\n\nDonation Drive Report:\n")
                    .append("\nTotal Blood Volume: ").append(totalBloodVolume).append(" Liters\n")
                    .append("\nBlood Types Collected: ").append(bloodTypesCollected.toString().replaceAll(", $", "")).append("\n")
                    .append("Blood Type A: ").append(bloodTypeA).append(" ml\n")
                    .append("Blood Type B: ").append(bloodTypeB).append(" ml\n")
                    .append("Blood Type AB: ").append(bloodTypeAB).append(" ml\n")
                    .append("Blood Type O: ").append(bloodTypeO).append(" ml\n")
                    .append("\nDonor Information:\n").append(donorInfo.toString());
        } else {
            report.append("No data available for donation drives.");
        }

        // Display the report
        reportTextView.setText(report.toString());
    }

    public void logOut(View view) {
        // Sign out from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Redirect to the SignIn activity
        Intent intent = new Intent(this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
        startActivity(intent);

        // Close the current activity
        finish();
    }
}