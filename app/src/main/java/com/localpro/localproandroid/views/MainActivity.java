package com.localpro.localproandroid.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.LoginViewModel;

public class MainActivity extends AppCompatActivity {
     private TextInputEditText etEmail, etPassword;
     private MaterialButton btnLogin;
     private LoginViewModel loginViewModel;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String existingToken = sharedPreferences.getString("auth_token", null);
        String savedRole = sharedPreferences.getString("user_role", null);
        String savedUserId = sharedPreferences.getString("user_id", null);

        if (existingToken != null) {
            Intent intent;
            if (savedRole != null && savedRole.equalsIgnoreCase("provider")) {
                boolean isOnboarded = sharedPreferences.getBoolean("is_onboarded_" + savedUserId, false);
                if (!isOnboarded) {
                    intent = new Intent(MainActivity.this, ProviderOnboardingActivity.class);
                    intent.putExtra("USER_ID", savedUserId);
                } else {
                    intent = new Intent(MainActivity.this, ProviderDashboardActivity.class);
                }
            } else {
                intent = new Intent(MainActivity.this, CustomerDashboardActivity.class);
            }
            startActivity(intent);
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupObservers();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }

            loginViewModel.login(email, password);
        });

        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setupObservers() {
        loginViewModel.getLoginResult().observe(this, authResponse -> {
            if (authResponse != null) {
                String token = authResponse.getToken();
                String role = authResponse.getUser().getRole();
                String userId = authResponse.getUser() != null ? authResponse.getUser().getId() : null;

                Toast.makeText(MainActivity.this, "Welcome " + role + "! Login Successful", Toast.LENGTH_LONG).show();

                SharedPreferences sharedPreferences = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("auth_token", token);
                editor.putString("user_role", role);
                
                boolean isOnboardedFromServer = false;
                if (userId != null) {
                    editor.putString("user_id", userId);
                    isOnboardedFromServer = authResponse.getUser().getProviderProfile() != null;
                    if (isOnboardedFromServer) {
                        editor.putBoolean("is_onboarded_" + userId, true);
                    }
                }
                editor.apply();

                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                Intent intent;
                if (role != null && role.equalsIgnoreCase("provider")) {
                    boolean isOnboarded = sharedPreferences.getBoolean("is_onboarded_" + userId, false) || isOnboardedFromServer;
                    if (!isOnboarded) {
                        intent = new Intent(MainActivity.this, ProviderOnboardingActivity.class);
                        intent.putExtra("USER_ID", userId);
                    } else {
                        intent = new Intent(MainActivity.this, ProviderDashboardActivity.class);
                    }
                } else {
                    intent = new Intent(MainActivity.this, CustomerDashboardActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });

        loginViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
