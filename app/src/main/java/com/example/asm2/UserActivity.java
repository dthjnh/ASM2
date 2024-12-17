package com.example.asm2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.AddDonationSiteUser.AddDonationSiteUserActivity;
import com.example.asm2.AdminandUserViewVolunteer.UserViewVolunteerActivity;
import com.example.asm2.DonorRegister.ViewDonorsActivity;
import com.example.asm2.Login.SignIn;
import com.example.asm2.MapView.MapsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView greetingTextView, phoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        greetingTextView = findViewById(R.id.greetingTextView);
        phoneTextView = findViewById(R.id.phoneTextView);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            fetchUserDetails(uid);
        } else {
            greetingTextView.setText("Hello, User");
            phoneTextView.setText("Phone: N/A");
        }

        // Khởi tạo BottomNavigationView
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                openHome();
                return true;
            } else if (itemId == R.id.nav_map) {
                openMap();
                return true;
            } else if (itemId == R.id.nav_logout) {
                logout();
                return true;
            } else {
                return false;
            }
        });
    }

    private void fetchUserDetails(String uid) {
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String fullName = documentSnapshot.getString("FullName");
                String phoneNumber = documentSnapshot.getString("Phone");

                if (fullName != null && !fullName.isEmpty()) {
                    greetingTextView.setText("Hello, " + fullName);
                } else {
                    greetingTextView.setText("Hello, User");
                }

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    phoneTextView.setText("Phone: " + phoneNumber);
                } else {
                    phoneTextView.setText("Phone: N/A");
                }
            } else {
                greetingTextView.setText("Hello, User");
                phoneTextView.setText("Phone: N/A");
            }
        }).addOnFailureListener(e -> {
            greetingTextView.setText("Hello, User");
            phoneTextView.setText("Phone: N/A");
        });
    }



    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(UserActivity.this, SignIn.class));
        finish();
    }

    private void openMap() {
        startActivity(new Intent(UserActivity.this, MapsActivity.class));
    }

    private void openHome() {
        startActivity(new Intent(UserActivity.this, UserActivity.class));
    }

    public void addDonationSite(View view) {
        startActivity(new Intent(UserActivity.this, AddDonationSiteUserActivity.class));
    }

    public void viewDonors(View view) {
        startActivity(new Intent(UserActivity.this, ViewDonorsActivity.class));
    }

    public void viewVolunteers(View view) {
        startActivity(new Intent(UserActivity.this, UserViewVolunteerActivity.class));
    }
}
