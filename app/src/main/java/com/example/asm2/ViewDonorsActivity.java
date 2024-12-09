package com.example.asm2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDonorsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donors);

        ListView listView = findViewById(R.id.listViewDonors);
        dbHelper = new DatabaseHelper(this);

        // Retrieve all donors from the database
        Cursor cursor = dbHelper.getAllDonors();

        if (cursor != null && cursor.getCount() > 0) {
            // Define columns to display
            String[] columns = {
                    DatabaseHelper.DONOR_COLUMN_NAME,
                    DatabaseHelper.DONOR_COLUMN_CONTACT,
                    DatabaseHelper.DONOR_COLUMN_SITE_ADDRESS
            };

            // Define layout views to bind data
            int[] views = {R.id.textViewName, R.id.textViewContact, R.id.textViewSiteAddress};

            // Create and set adapter
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.item_donors, // Custom layout for donor list item
                    cursor,
                    columns,
                    views,
                    0
            );

            listView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No donors registered yet", Toast.LENGTH_SHORT).show();
        }

    }
    public void backToAdmin(View view) {
        finish(); // Close this activity and return to the Admin activity
    }
}