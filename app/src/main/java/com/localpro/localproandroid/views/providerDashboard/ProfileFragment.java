package com.localpro.localproandroid.views.providerDashboard;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.repositories.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.Context.MODE_PRIVATE;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    @Inject
    UserRepository userRepository;

    private TextView tvProfileInitials, tvProfileName, tvProfilePhone, tvProfileBio;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String name = prefs.getString("provider_name", "Provider");
        String email = prefs.getString("provider_email", "N/A");

        // Bind Views
        tvProfileInitials = view.findViewById(R.id.tvProfileInitials);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        TextView tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        TextView tvProfileCategory = view.findViewById(R.id.tvProfileCategory);
        tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        tvProfileBio = view.findViewById(R.id.tvProfileBio);

        // Initials
        if (name != null && !name.trim().isEmpty()) {
            tvProfileInitials.setText(String.valueOf(name.trim().charAt(0)).toUpperCase());
        } else {
            tvProfileInitials.setText("P");
        }

        // Name
        tvProfileName.setText(name);

        // Email
        tvProfileEmail.setText(email);

        // Category from SharedPreferences
        String category = prefs.getString("provider_category", null);
        if (category != null && !category.trim().isEmpty()) {
            tvProfileCategory.setText("🔧 " + category);
        }

        // Phone
        String phone = prefs.getString("provider_phone", null);
        if (phone != null && !phone.trim().isEmpty()) {
            tvProfilePhone.setText(phone);
        } else {
            tvProfilePhone.setText("Not set");
        }

        // Stats
        TextView tvStatMemberSince = view.findViewById(R.id.tvStatMemberSince);
        tvStatMemberSince.setText("2026");

        TextView tvStatJobsDone = view.findViewById(R.id.tvStatJobsDone);
        int jobsDone = prefs.getInt("total_jobs_done", 0);
        tvStatJobsDone.setText(String.valueOf(jobsDone));

        TextView tvStatRating = view.findViewById(R.id.tvStatRating);
        float rating = prefs.getFloat("provider_rating", 0f);
        if (rating > 0) {
            tvStatRating.setText(String.format("%.1f", rating));
        } else {
            tvStatRating.setText("--");
        }

        // Bio
        String bio = prefs.getString("provider_bio", null);
        if (bio != null && !bio.trim().isEmpty()) {
            tvProfileBio.setText(bio);
        } else {
            tvProfileBio.setText("Bio not set");
        }

        // Edit Profile Button Setup
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> showEditProfileDialog());
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogDark);
        builder.setTitle("Edit Profile");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        final EditText etName = new EditText(requireContext());
        etName.setHint("Name");
        etName.setHintTextColor(Color.GRAY);
        etName.setTextColor(Color.WHITE);
        etName.setText(tvProfileName.getText().toString());
        layout.addView(etName);

        final EditText etPhone = new EditText(requireContext());
        etPhone.setHint("Phone Number");
        etPhone.setHintTextColor(Color.GRAY);
        etPhone.setTextColor(Color.WHITE);
        String currentPhone = tvProfilePhone.getText().toString();
        etPhone.setText(currentPhone.equals("Not set") ? "" : currentPhone);
        layout.addView(etPhone);

        final EditText etBio = new EditText(requireContext());
        etBio.setHint("Bio");
        etBio.setHintTextColor(Color.GRAY);
        etBio.setTextColor(Color.WHITE);
        String currentBio = tvProfileBio.getText().toString();
        etBio.setText(currentBio.equals("Bio not set") ? "" : currentBio);
        layout.addView(etBio);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String nameStr = etName.getText().toString().trim();
            String phoneStr = etPhone.getText().toString().trim();
            String bioStr = etBio.getText().toString().trim();

            if (nameStr.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            userRepository.updateProfile(nameStr, phoneStr, bioStr, null).enqueue(new retrofit2.Callback<com.localpro.localproandroid.models.AuthResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<com.localpro.localproandroid.models.AuthResponse> call, @NonNull retrofit2.Response<com.localpro.localproandroid.models.AuthResponse> response) {
                    if (response.isSuccessful()) {
                        userRepository.saveUserProfile(nameStr, phoneStr, bioStr);
                        
                        // Update UI views directly
                        tvProfileName.setText(nameStr);
                        tvProfilePhone.setText(phoneStr.isEmpty() ? "Not set" : phoneStr);
                        tvProfileBio.setText(bioStr.isEmpty() ? "Bio not set" : bioStr);
                        tvProfileInitials.setText(String.valueOf(nameStr.charAt(0)).toUpperCase());

                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error updating profile. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<com.localpro.localproandroid.models.AuthResponse> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}