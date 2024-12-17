package com.example.asm2.Login;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editEmailSignUp, editPasswordSignUp;
    private Button btnSignUp;
    private TextView signInPrompt;
    private ImageView imageViewTogglePasswordSignUp;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        editEmailSignUp = findViewById(R.id.editEmail);
        editPasswordSignUp = findViewById(R.id.editPasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        signInPrompt = findViewById(R.id.signInPrompt);
        imageViewTogglePasswordSignUp = findViewById(R.id.imageViewTogglePasswordSignUp);

        imageViewTogglePasswordSignUp.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editPasswordSignUp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageViewTogglePasswordSignUp.setImageResource(R.drawable.ic_eye);
            } else {
                editPasswordSignUp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageViewTogglePasswordSignUp.setImageResource(R.drawable.ic_eye_off);
            }
            isPasswordVisible = !isPasswordVisible;
            editPasswordSignUp.setSelection(editPasswordSignUp.length());
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmailSignUp.getText().toString();
                String password = editPasswordSignUp.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SignUp.this, "Failed to sign up", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        signInPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}