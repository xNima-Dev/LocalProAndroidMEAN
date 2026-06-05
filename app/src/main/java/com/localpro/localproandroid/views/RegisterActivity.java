package com.localpro.localproandroid.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterName, etRegisterEmail, etRegisterPassword, etRegisterPhone;
    private Spinner spinnerRole;
    private Button btnRegister;
    private TextView tvLoginLink;

    private RegisterViewModel registerViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        etRegisterPhone = findViewById(R.id.etRegisterPhone);

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        String[] roles = {"customer", "provider"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        setupObservers();

        btnRegister.setOnClickListener(v -> {
            String name = etRegisterName.getText().toString().trim();
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();
            String selectedRole = spinnerRole.getSelectedItem().toString();
            String phoneNumber = etRegisterPhone.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                registerViewModel.register(name, email, password, selectedRole, phoneNumber);
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

        private void setupObservers() {
            registerViewModel.getRegisterResult().observe(this, authResponse -> {
                if (authResponse != null) {
                    String token = authResponse.getToken();
                    String role = authResponse.getUser().getRole();

                    SharedPreferences sharedPreferences = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("auth_token", token);
                    editor.putString("user_role", role);
                    editor.apply();

                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent;
                    if (role != null && role.equalsIgnoreCase("provider")) {
                        intent = new Intent(RegisterActivity.this, ProviderDashboardActivity.class);
                    }else {
                        intent = new Intent(RegisterActivity.this, CustomerDashboardActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            });

            registerViewModel.getErrorMessage().observe(this, error -> {
                if (error != null) {
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }