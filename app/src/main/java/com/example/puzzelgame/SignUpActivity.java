package com.example.puzzelgame;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.puzzelgame.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etPhone;
    Button btnSignUp;

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();

                    User user = new User(name, email, phone);
                    userRef.child(uid).setValue(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                finish(); // or redirect to login/home
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to save user", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Sign up failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}