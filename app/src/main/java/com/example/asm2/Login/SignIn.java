package com.example.asm2.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Admin;
import com.example.asm2.SuperUserActivity;
import com.example.asm2.UserActivity;
import com.example.asm2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private EditText editEmailSignIn, editPasswordSignIn;
    private Button btnSignIn;
    private TextView signUpPrompt;
    private ImageView imageViewTogglePassword;
    private Boolean valid = true;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        editEmailSignIn = findViewById(R.id.editEmailSignIn);
        editPasswordSignIn = findViewById(R.id.editPasswordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        signUpPrompt = findViewById(R.id.signUpPrompt);
        imageViewTogglePassword = findViewById(R.id.imageViewTogglePassword);

        imageViewTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editPasswordSignIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageViewTogglePassword.setImageResource(R.drawable.ic_eye);
            } else {
                editPasswordSignIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageViewTogglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            isPasswordVisible = !isPasswordVisible;
            editPasswordSignIn.setSelection(editPasswordSignIn.length());
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(editEmailSignIn);
                checkField(editPasswordSignIn);

                if(valid){
                    auth.signInWithEmailAndPassword(editEmailSignIn.getText().toString(), editPasswordSignIn.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(SignIn.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                                    checkIUserAccessLevel(authResult.getUser().getUid());
                                    startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignIn.this, "Failed to sign in", Toast.LENGTH_SHORT).show();
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

    private void checkIUserAccessLevel(String uid) {
        DocumentReference df = fstore.collection("users").document(uid);
        df.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.getString("isSuperUser") != null) {
                startActivity(new Intent(getApplicationContext(), SuperUserActivity.class));
                finish();
            } else if (documentSnapshot.getString("isAdmin") != null) {
                startActivity(new Intent(getApplicationContext(), Admin.class));
                finish();
            } else if (documentSnapshot.getString("isUser") != null) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                finish();
            } else {
                Toast.makeText(SignIn.this, "Access level undefined!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SignIn.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            checkIUserAccessLevel(auth.getCurrentUser().getUid());
        }
    }
}