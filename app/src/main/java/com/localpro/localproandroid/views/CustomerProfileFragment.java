package com.localpro.localproandroid.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.localpro.localproandroid.R;

public class CustomerProfileFragment extends Fragment {
    public CustomerProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        SharedPreferences prefs = requireActivity().getSharedPreferences("LocalProPrefs", Context.MODE_PRIVATE);
        String name = prefs.getString("customer_name", "Customer");
        String email = prefs.getString("provider_email", "customer@localpro.com"); // Reusing email field

        android.widget.TextView tvName = view.findViewById(R.id.tvProfileName);
        android.widget.TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        android.widget.TextView tvInitials = view.findViewById(R.id.tvProfileInitials);

        tvName.setText(name);
        tvEmail.setText(email);
        
        if (name != null && !name.isEmpty()) {
            tvInitials.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
        
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().remove("auth_token").apply();
            
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}
