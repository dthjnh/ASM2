package com.example.asm2.Login;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.asm2.Admin;
import com.example.asm2.MainActivity;
import com.example.asm2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private EditText editFullName, editEmail, editPassword, editPhone, editDateofBirth;
    private Button btnSignUp;
    private TextView signInPrompt;
    private ImageButton btnDatePicker;
    private Boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPhone = findViewById(R.id.editPhone);
        editDateofBirth = findViewById(R.id.editDateofBirth);
        btnSignUp = findViewById(R.id.btnSignUp);
        signInPrompt = findViewById(R.id.signInPrompt);
        btnDatePicker = findViewById(R.id.btnDatePicker);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnSignUp.setOnClickListener(v -> {
            checkField(editFullName);
            checkField(editEmail);
            checkField(editPassword);
            checkField(editPhone);
            checkField(editDateofBirth);

            if (valid) {
                // Start user registration
                auth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            Toast.makeText(SignUp.this, "User created successfully. Please sign in.", Toast.LENGTH_SHORT).show();

                            // Save user info to Firestore
                            DocumentReference df = fstore.collection("users").document(auth.getCurrentUser().getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("FullName", editFullName.getText().toString());
                            userInfo.put("Email", editEmail.getText().toString());
                            userInfo.put("Phone", editPhone.getText().toString());
                            userInfo.put("DateOfBirth", editDateofBirth.getText().toString());

                            // Specify user role
                            if (editEmail.getText().toString().endsWith("@admin.com")) {
                                userInfo.put("isAdmin", "1"); // Admin
                            } else {
                                userInfo.put("isUser", "1"); // Regular User
                            }

                            df.set(userInfo).addOnSuccessListener(aVoid -> {
                                // Log out the user
                                auth.signOut();

                                // Redirect to SignIn page
                                Intent intent = new Intent(SignUp.this, SignIn.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // Close the SignUp activity
                            }).addOnFailureListener(e -> {
                                Toast.makeText(SignUp.this, "Failed to save user info", Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(SignUp.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        signInPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUp.this,
                (view, year1, month1, dayOfMonth) -> editDateofBirth.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }
}


