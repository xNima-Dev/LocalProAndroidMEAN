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
        
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("LocalProPrefs", Context.MODE_PRIVATE);
            prefs.edit().remove("auth_token").apply();
            
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}
