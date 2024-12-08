package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    // Logout method linked to the button
    public void logoutAdmin(View view) {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        startActivity(new Intent(Admin.this, SignIn.class)); // Redirect to SignIn activity
        finish(); // Close the Admin activity
    }
}