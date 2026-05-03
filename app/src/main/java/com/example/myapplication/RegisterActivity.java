package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnBack = findViewById(R.id.btnBack);
        EditText etFullName = findViewById(R.id.etFullName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String name = etFullName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                if (password.equals(confirm)) {
                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}
