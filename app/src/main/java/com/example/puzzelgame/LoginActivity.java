package com.example.puzzelgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());
        tvGoToSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, FirstActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
