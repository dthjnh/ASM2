package com.example.asm2.Info;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountInfoActivity extends AppCompatActivity {

    private EditText editFullName, editDateOfBirth, editEmail, editPhone;
    private Button btnSaveChanges;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        editFullName = findViewById(R.id.editFullName);
        editDateOfBirth = findViewById(R.id.editDateOfBirth);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Fetch user data
        fetchUserData();

        // Save changes
        btnSaveChanges.setOnClickListener(v -> saveUserData());
    }

    private void fetchUserData() {
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                editFullName.setText(documentSnapshot.getString("FullName"));
                editDateOfBirth.setText(documentSnapshot.getString("DateOfBirth"));
                editEmail.setText(documentSnapshot.getString("Email"));
                editPhone.setText(documentSnapshot.getString("Phone"));
            } else {
                Toast.makeText(AccountInfoActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AccountInfoActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserData() {
        String fullName = editFullName.getText().toString();
        String dateOfBirth = editDateOfBirth.getText().toString();
        String email = editEmail.getText().toString();
        String phone = editPhone.getText().toString();

        // Validate fields
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(dateOfBirth) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update Firestore
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        Map<String, Object> userData = new HashMap<>();
        userData.put("FullName", fullName);
        userData.put("DateOfBirth", dateOfBirth);
        userData.put("Email", email);
        userData.put("Phone", phone);

        userRef.update(userData).addOnSuccessListener(aVoid -> {
            Toast.makeText(AccountInfoActivity.this, "User info updated successfully!", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            Toast.makeText(AccountInfoActivity.this, "Failed to update info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
        startActivity(new Intent(AccountInfoActivity.this, UserProfileActivity.class));
    }


}