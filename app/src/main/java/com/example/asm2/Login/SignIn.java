package com.example.asm2.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2.Admin;
import com.example.asm2.MainActivity;
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
    private Boolean valid = true;

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
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        // Extract the data from Firestore
        df.get().addOnSuccessListener(documentSnapshot -> {
            Log.d("TAG", "onSuccess: " + documentSnapshot.getData());

            // Check if the user is admin
            if (documentSnapshot.getString("isAdmin") != null) {
                // User is admin
                startActivity(new Intent(getApplicationContext(), Admin.class));
                finish();
            } else if (documentSnapshot.getString("isUser") != null) {
                // User is regular user
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                // Handle unexpected cases
                Toast.makeText(SignIn.this, "Access level undefined!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("TAG", "Error fetching user data", e);
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
            // Check user role and redirect
            checkIUserAccessLevel(auth.getCurrentUser().getUid());
        }
    }
}