package com.example.asm2.AddDonationEditandDelete;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Database.DonationSitesDatabaseHelper;
import com.example.asm2.R;

import java.util.List;

public class DonationSiteListActivity extends AppCompatActivity {

    private ListView listView;
    private DonationSitesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_site_list);

        listView = findViewById(R.id.listView);
        dbHelper = new DonationSitesDatabaseHelper(this);

        List<DonationSite> donationSites = dbHelper.getAllDonationSites();
        DonationSiteAdapter adapter = new DonationSiteAdapter(this, donationSites);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    // Refresh the data and update the ListView
    private void refreshList() {
        List<DonationSite> donationSites = dbHelper.getAllDonationSites();
        DonationSiteAdapter adapter = new DonationSiteAdapter(this, donationSites);
        listView.setAdapter(adapter); // Update the ListView with the new adapter
    }
}