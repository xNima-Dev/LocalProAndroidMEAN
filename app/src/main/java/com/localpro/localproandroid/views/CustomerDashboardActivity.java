package com.localpro.localproandroid.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.localpro.localproandroid.R;

public class CustomerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.customer_bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_customer_home) {
                selectedFragment = new CustomerHomeFragment();
            } else if (itemId == R.id.nav_customer_bookings) {
                selectedFragment = new CustomerBookingsFragment();
            } else if (itemId == R.id.nav_customer_profile) {
                selectedFragment = new CustomerProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.customer_fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_customer_home);
        }
    }
}