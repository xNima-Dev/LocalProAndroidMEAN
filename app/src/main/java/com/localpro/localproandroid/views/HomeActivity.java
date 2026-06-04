package com.localpro.localproandroid.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.localpro.localproandroid.R;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUserRole = findViewById(R.id.tvUserRole);

        SharedPreferences sharedPreferences = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String role = sharedPreferences.getString("user_role", "User");
        String token = sharedPreferences.getString("auth_token", null);

        tvUserRole.setText("Logged in as: " + role.toUpperCase());
    }
}