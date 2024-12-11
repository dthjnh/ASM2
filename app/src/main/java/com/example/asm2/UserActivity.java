package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.AddDonationSiteUser.AddDonationSiteUserActivity;
import com.example.asm2.AdminandUserViewVolunteer.UserViewVolunteerActivity;
import com.example.asm2.DonorRegister.ViewDonorsActivity;
import com.example.asm2.Login.SignIn;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

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

    public void logoutUser(View view) {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        startActivity(new Intent(UserActivity.this, SignIn.class));
        finish(); // Close the Admin activity
    }
}