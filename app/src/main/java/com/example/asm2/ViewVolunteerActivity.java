package com.example.asm2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class ViewVolunteerActivity extends AppCompatActivity {

    private ListView lvVolunteers;
    private VolunteerDatabaseHelper volunteerDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_volunteers);

        lvVolunteers = findViewById(R.id.lvVolunteers);
        volunteerDatabaseHelper = new VolunteerDatabaseHelper(this);

        loadVolunteers();

        lvVolunteers.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ViewVolunteerActivity.this, EditVolunteerActivity.class);
            intent.putExtra("VOLUNTEER_ID", id);
            startActivity(intent);
        });
    }

    private void loadVolunteers() {
        Cursor cursor = volunteerDatabaseHelper.getAllVolunteers();
        String[] from = {VolunteerDatabaseHelper.COLUMN_NAME, VolunteerDatabaseHelper.COLUMN_PHONE};
        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, from, to, 0);
        lvVolunteers.setAdapter(adapter);
    }
}
