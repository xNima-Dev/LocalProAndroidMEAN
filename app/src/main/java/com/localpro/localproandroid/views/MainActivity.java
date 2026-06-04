package com.localpro.localproandroid.views;

import android.os.Bundle;
import android.text.TextUtils;
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
    }

    private void setupObservers() {
        loginViewModel.getLoginResult().observe(this, authResponse -> {
            if (authResponse != null) {
                String token = authResponse.getToken();
                String role = authResponse.getUser().getRole();

                Toast.makeText(MainActivity.this, "Welcome " + role + "! Login Successful", Toast.LENGTH_LONG).show();

                // 🛑 ඉස්සරහට අපි මේ Token එක SharedPreferences වල සේව් කරලා, Home Activity එකට යූසර්ව යවනවා...
            }
        });

        loginViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
