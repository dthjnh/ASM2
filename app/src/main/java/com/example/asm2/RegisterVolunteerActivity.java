package com.example.asm2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterVolunteerActivity extends AppCompatActivity {

    private EditText etVolunteerName, etVolunteerPhone;
    private Button btnSubmitVolunteer;
    private VolunteerDatabaseHelper volunteerDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_volunteer);

        etVolunteerName = findViewById(R.id.etVolunteerName);
        etVolunteerPhone = findViewById(R.id.etVolunteerPhone);
        btnSubmitVolunteer = findViewById(R.id.btnSubmitVolunteer);

        volunteerDatabaseHelper = new VolunteerDatabaseHelper(this);

        btnSubmitVolunteer.setOnClickListener(v -> {
            String name = etVolunteerName.getText().toString().trim();
            String phone = etVolunteerPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(RegisterVolunteerActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean isInserted = volunteerDatabaseHelper.insertVolunteer(name, phone);
                if (isInserted) {
                    Toast.makeText(RegisterVolunteerActivity.this, "Volunteer Registered", Toast.LENGTH_SHORT).show();
                    etVolunteerName.setText("");
                    etVolunteerPhone.setText("");
                } else {
                    Toast.makeText(RegisterVolunteerActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
