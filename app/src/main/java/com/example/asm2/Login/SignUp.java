package com.example.asm2.Login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Admin;
import com.example.asm2.R;
import com.example.asm2.SuperUserActivity;
import com.example.asm2.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText editFullName, editDateofBirth, editEmail, editPhone, editPasswordSignUp;
    private Button btnSignUp;
    private TextView signInPrompt;
    private ImageView imageViewTogglePasswordSignUp;
    private ImageButton btnDatePicker;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editFullName = findViewById(R.id.editFullName);
        editDateofBirth = findViewById(R.id.editDateofBirth);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editPasswordSignUp = findViewById(R.id.editPasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        signInPrompt = findViewById(R.id.signInPrompt);
        imageViewTogglePasswordSignUp = findViewById(R.id.imageViewTogglePasswordSignUp);
        btnDatePicker = findViewById(R.id.btnDatePicker);

        // Toggle password visibility
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

        // Date Picker Functionality
        btnDatePicker.setOnClickListener(v -> showDatePicker());

        // Sign Up Button Click Listener
        btnSignUp.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString();
            String dateOfBirth = editDateofBirth.getText().toString();
            String email = editEmail.getText().toString();
            String phone = editPhone.getText().toString();
            String password = editPasswordSignUp.getText().toString();

            // Validate fields
            if (fullName.isEmpty() || dateOfBirth.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            saveUserDetails(userId, fullName, dateOfBirth, email, phone);

                            // Navigate based on email domain
                            if (email.endsWith("@admin.com")) {
                                startActivity(new Intent(SignUp.this, Admin.class));
                                Toast.makeText(SignUp.this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                            } else if (email.endsWith("@super.com")) {
                                startActivity(new Intent(SignUp.this, SuperUserActivity.class));
                                Toast.makeText(SignUp.this, "Logged in as Super User", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(SignUp.this, UserActivity.class));
                                Toast.makeText(SignUp.this, "Logged in as User", Toast.LENGTH_SHORT).show();
                            }

                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUp.this, "Failed to sign up: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Navigate to Sign In Screen
        signInPrompt.setOnClickListener(v -> finish());
    }

    // Method to Show Date Picker Dialog
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            editDateofBirth.setText(selectedDate); // Set the selected date in EditText
        }, year, month, day);

        datePickerDialog.show();
    }

    // Save user details to Firestore
    private void saveUserDetails(String userId, String fullName, String dateOfBirth, String email, String phone) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("FullName", fullName);
        userDetails.put("DateOfBirth", dateOfBirth);
        userDetails.put("Email", email);
        userDetails.put("Phone", phone);

        if (email.endsWith("@admin.com")) {
            userDetails.put("Role", "Admin");
        } else if (email.endsWith("@super.com")) {
            userDetails.put("Role", "SuperUser");
        } else {
            userDetails.put("Role", "User");
        }

        firestore.collection("users").document(userId)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> Log.d("SignUp", "User details saved successfully"))
                .addOnFailureListener(e -> Log.e("SignUp", "Error saving user details", e));
    }
}