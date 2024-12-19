package com.example.asm2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.asm2.AddDonationSiteUser.AddDonationSiteUserActivity;
import com.example.asm2.AdminandUserViewVolunteer.UserViewVolunteerActivity;
import com.example.asm2.DonorRegister.ViewDonorsActivity;
import com.example.asm2.Fragment.GridFragment;
import com.example.asm2.Fragment.HeaderFragment;
import com.example.asm2.Info.UserProfileActivity;
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
    private HeaderFragment headerFragment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Set the HeaderFragment
        headerFragment = new HeaderFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.headerFragmentContainer, headerFragment)
                .commit();

        // Set the GridFragment
        fragmentManager.beginTransaction()
                .replace(R.id.gridFragmentContainer, new GridFragment())
                .commit();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            fetchUserDetails(uid);
        } else {
            updateGreetingText("Hello, User");
            updatePhoneText("Phone: N/A");
        }

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
            } else if (itemId == R.id.nav_profile) {
                openProfile();
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
                    updateGreetingText("Hello, " + fullName);
                } else {
                    updateGreetingText("Hello, User");
                }

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    updatePhoneText(phoneNumber);
                } else {
                    updatePhoneText("Phone: N/A");
                }
            } else {
                updateGreetingText("Hello, User");
                updatePhoneText("Phone: N/A");
            }
        }).addOnFailureListener(e -> {
            updateGreetingText("Hello, User");
            updatePhoneText("Phone: N/A");
        });
    }

    private void updateGreetingText(String text) {
        if (headerFragment != null) {
            headerFragment.updateGreetingText(text);
        }
    }

    private void updatePhoneText(String text) {
        if (headerFragment != null) {
            headerFragment.updatePhoneText(text);
        }
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

    private void openProfile() {
        startActivity(new Intent(UserActivity.this, UserProfileActivity.class));
    }
}