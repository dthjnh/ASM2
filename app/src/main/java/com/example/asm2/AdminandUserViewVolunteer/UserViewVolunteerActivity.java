package com.example.asm2.AdminandUserViewVolunteer;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.R;

public class UserViewVolunteerActivity extends AppCompatActivity {

    private ListView lvVolunteers;
    private VolunteerDatabaseHelper volunteerDatabaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_volunteer);

        lvVolunteers = findViewById(R.id.lvVolunteers);
        volunteerDatabaseHelper = new VolunteerDatabaseHelper(this);

        loadVolunteers();
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
