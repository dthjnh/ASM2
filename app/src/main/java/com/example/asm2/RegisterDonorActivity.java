package com.example.asm2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterDonorActivity extends AppCompatActivity {

    private EditText editName, editContact;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_donor);

        editName = findViewById(R.id.editName);
        editContact = findViewById(R.id.editContact);
        btnSubmit = findViewById(R.id.btnSubmit);

        dbHelper = new DatabaseHelper(this);

        // Get the site address from the intent
        String siteAddress = getIntent().getStringExtra("site_address");
        Toast.makeText(this, "Registering for site: " + siteAddress, Toast.LENGTH_SHORT).show();

        // Submit registration
        btnSubmit.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String contact = editContact.getText().toString().trim();

            if (name.isEmpty() || contact.isEmpty() || siteAddress == null || siteAddress.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Call registerDonor method
                boolean success = dbHelper.registerDonor(name, contact, siteAddress);

                if (success) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}