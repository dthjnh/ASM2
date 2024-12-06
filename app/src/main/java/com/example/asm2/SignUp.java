package com.example.asm2;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Calendar;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText editFullName, editEmail, editPassword, editPhone, editDateofBirth;
    private Button btnSignUp;
    private TextView signInPrompt;
    private ImageButton btnDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = editFullName.getText().toString();
                String emailAddress = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                String phone = editPhone.getText().toString();
                String dateOfBirth = editDateofBirth.getText().toString();

                if (fullName.isEmpty() || emailAddress.isEmpty() || password.isEmpty() || phone.isEmpty() || dateOfBirth.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Create a new user with the provided details
                            User user = new User(fullName, emailAddress, dateOfBirth);

                            // Store user data in Firestore
                            db.collection("users").document(auth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUp.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUp.this, SignIn.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUp.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signInPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });
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


