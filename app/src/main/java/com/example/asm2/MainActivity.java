package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Login.SignIn;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void logoutUser(View view) {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        startActivity(new Intent(MainActivity.this, SignIn.class)); // Redirect to SignIn activity
        finish(); // Close the Admin activity
    }
}