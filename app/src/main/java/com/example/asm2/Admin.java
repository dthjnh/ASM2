package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.AddDonationEditandDelete.AddDonationSiteActivity;
import com.example.asm2.AdminandUserViewVolunteer.RegisterVolunteerActivity;
import com.example.asm2.AdminandUserViewVolunteer.ViewVolunteerActivity;
import com.example.asm2.DonationDrive.DonationDriveActivity;
import com.example.asm2.DonorRegister.ViewDonorsActivity;
import com.example.asm2.Login.SignIn;
import com.example.asm2.MapView.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Admin.this, SignIn.class));
        finish();
    }

    public void addDonationSite(View view) {
        startActivity(new Intent(Admin.this, AddDonationSiteActivity.class));
    }

    public void viewDonors(View view) {
        startActivity(new Intent(Admin.this, ViewDonorsActivity.class));
    }

    public void donationDrive(View view) {
        startActivity(new Intent(Admin.this, DonationDriveActivity.class));
    }

    public void registerVolunteer(View view) {
        startActivity(new Intent(Admin.this, RegisterVolunteerActivity.class));
    }

    public void viewVolunteers(View view) {
        startActivity(new Intent(Admin.this, ViewVolunteerActivity.class));
    }
}