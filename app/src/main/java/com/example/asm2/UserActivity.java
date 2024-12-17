package com.example.asm2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.AddDonationSiteUser.AddDonationSiteUserActivity;
import com.example.asm2.AdminandUserViewVolunteer.UserViewVolunteerActivity;
import com.example.asm2.DonorRegister.ViewDonorsActivity;
import com.example.asm2.Login.SignIn;
import com.example.asm2.MapView.MapsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

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