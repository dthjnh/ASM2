package com.example.asm2.DonorRegister;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Database.DonorsDatabaseHelper;
import com.example.asm2.R;

public class ViewDonorsActivity extends AppCompatActivity {

    private DonorsDatabaseHelper dbHelper;
    private ListView listViewDonors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donors);

        dbHelper = new DonorsDatabaseHelper(this);
        listViewDonors = findViewById(R.id.listViewDonors);

        loadDonors();

        // Handle click event for edit and delete options
        listViewDonors.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            showEditDeleteDialog((int) id);
        });
    }

    private void loadDonors() {
        Cursor cursor = dbHelper.getAllDonors();
        String[] from = {"name", "contact", "site_address"};
        int[] to = {R.id.textViewName, R.id.textViewContact, R.id.textViewSiteAddress};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_donors, cursor, from, to, 0);
        listViewDonors.setAdapter(adapter);
    }

    private void showEditDeleteDialog(int donorId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action")
                .setItems(new String[]{"Edit", "Delete"}, (DialogInterface dialog, int which) -> {
                    if (which == 0) {
                        // Edit
                        Intent intent = new Intent(ViewDonorsActivity.this, EditDonorActivity.class);
                        intent.putExtra("donor_id", donorId);
                        startActivity(intent);
                    } else if (which == 1) {
                        // Delete
                        deleteDonor(donorId);
                    }
                });
        builder.show();
    }

    private void deleteDonor(int donorId) {
        boolean isDeleted = dbHelper.deleteDonor(donorId);
        if (isDeleted) {
            Toast.makeText(this, "Donor deleted successfully", Toast.LENGTH_SHORT).show();
            loadDonors(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to delete donor", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadDonors(); // Reload donor list
    }
}