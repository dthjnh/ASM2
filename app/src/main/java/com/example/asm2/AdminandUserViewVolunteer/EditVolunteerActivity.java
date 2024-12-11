package com.example.asm2.AdminandUserViewVolunteer;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.R;

public class EditVolunteerActivity extends AppCompatActivity {

    private EditText etEditVolunteerName, etEditVolunteerPhone;
    private Button btnUpdateVolunteer, btnDeleteVolunteer;
    private VolunteerDatabaseHelper volunteerDatabaseHelper;
    private long volunteerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_volunteer);

        etEditVolunteerName = findViewById(R.id.etEditVolunteerName);
        etEditVolunteerPhone = findViewById(R.id.etEditVolunteerPhone);
        btnUpdateVolunteer = findViewById(R.id.btnUpdateVolunteer);
        btnDeleteVolunteer = findViewById(R.id.btnDeleteVolunteer);

        volunteerDatabaseHelper = new VolunteerDatabaseHelper(this);

        volunteerId = getIntent().getLongExtra("VOLUNTEER_ID", -1);
        loadVolunteerData(volunteerId);

        btnUpdateVolunteer.setOnClickListener(v -> {
            String name = etEditVolunteerName.getText().toString().trim();
            String phone = etEditVolunteerPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(EditVolunteerActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean isUpdated = volunteerDatabaseHelper.updateVolunteer((int) volunteerId, name, phone);
                if (isUpdated) {
                    Toast.makeText(EditVolunteerActivity.this, "Volunteer Updated", Toast.LENGTH_SHORT).show();
                    etEditVolunteerName.setText("");
                    etEditVolunteerPhone.setText("");
                    finish();
                } else {
                    Toast.makeText(EditVolunteerActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteVolunteer.setOnClickListener(v -> {
            boolean isDeleted = volunteerDatabaseHelper.deleteVolunteer((int) volunteerId);
            if (isDeleted) {
                Toast.makeText(EditVolunteerActivity.this, "Volunteer Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditVolunteerActivity.this, "Deletion Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVolunteerData(long id) {
        Cursor cursor = volunteerDatabaseHelper.getVolunteerById((int) id);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(VolunteerDatabaseHelper.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(VolunteerDatabaseHelper.COLUMN_PHONE));
            etEditVolunteerName.setText(name);
            etEditVolunteerPhone.setText(phone);
            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVolunteerData(volunteerId);
    }
}
