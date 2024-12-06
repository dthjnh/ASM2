package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm2.MainActivity;
import com.example.asm2.R;
import com.example.asm2.SignUp;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editEmailSignIn, editPasswordSignIn;
    private Button btnSignIn;
    private TextView signUpPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();
        editEmailSignIn = findViewById(R.id.editEmailSignIn);
        editPasswordSignIn = findViewById(R.id.editPasswordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        signUpPrompt = findViewById(R.id.signUpPrompt);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = editEmailSignIn.getText().toString();
                String password = editPasswordSignIn.getText().toString();

                if (emailAddress.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignIn.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this, MainActivity.class));
                            finish(); // Optional: Call finish() to close the SignIn activity
                        } else {
                            Toast.makeText(SignIn.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signUpPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });



    }
}