package com.example.asm2.Info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Login.SignIn;
import com.example.asm2.MapView.MapsActivity;
import com.example.asm2.R;
import com.example.asm2.UserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView profileName, profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            fetchUserProfile(uid);
        } else {
            // Default info if no user is logged in
            profileName.setText("Guest User");
            profileEmail.setText("No email");
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                openHome();
                return true;
            } else if (itemId == R.id.nav_map) {
                openMap();
                return true;
            } else if (itemId == R.id.nav_profile) {
                openProfile();
                return true;
            } else {
                return false;
            }
        });
    }

    private void openProfile() {
        startActivity(new Intent(UserProfileActivity.this, UserProfileActivity.class));
    }

    private void openMap() {
        startActivity(new Intent(UserProfileActivity.this, MapsActivity.class));
    }

    private void openHome() {
        startActivity(new Intent(UserProfileActivity.this, UserActivity.class));
    }

    private void fetchUserProfile(String uid) {
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String fullName = documentSnapshot.getString("FullName");
                String email = documentSnapshot.getString("Email");

                if (fullName != null && !fullName.isEmpty()) {
                    profileName.setText(fullName);
                } else {
                    profileName.setText("User");
                }
                if (email != null && !email.isEmpty()) {
                    profileEmail.setText(email);
                } else {
                    profileEmail.setText("No email");
                }
            } else {
                Toast.makeText(UserProfileActivity.this, "User profile not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(UserProfileActivity.this, "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void openAccountInfo(View view) {
        // Start an activity for Account Information
        Intent intent = new Intent(UserProfileActivity.this, AccountInfoActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(UserProfileActivity.this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
    }

    public void deleteAccount(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Proceed with account deletion
                        user.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfileActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserProfileActivity.this, SignIn.class));
                                finish();
                            } else {
                                Toast.makeText(UserProfileActivity.this, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}