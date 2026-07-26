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
import com.localpro.localproandroid.viewmodels.ProfileViewModel;
import com.localpro.localproandroid.viewmodels.ProviderDashboardHomeViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.Context.MODE_PRIVATE;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private TextView tvProfileInitials, tvProfileName, tvProfilePhone, tvProfileBio;
    private TextView tvVerifiedIcon, tvVerifiedText, tvProfileRating, tvProfileReviews;
    private LinearLayout llVerifiedBadge;
    
    private ProviderDashboardHomeViewModel homeViewModel;
    private ProfileViewModel profileViewModel;

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

        // Verified Badge
        llVerifiedBadge = view.findViewById(R.id.llVerifiedBadge);
        tvVerifiedIcon = view.findViewById(R.id.tvVerifiedIcon);
        tvVerifiedText = view.findViewById(R.id.tvVerifiedText);
        
        // Ratings
        tvProfileRating = view.findViewById(R.id.tvProfileRating);
        tvProfileReviews = view.findViewById(R.id.tvProfileReviews);

        // Edit Profile Button Setup
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> showEditProfileDialog());
        
        // Bind Stats from shared HomeViewModel
        homeViewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(ProviderDashboardHomeViewModel.class);
        
        homeViewModel.getJobsDone().observe(getViewLifecycleOwner(), jobs -> {
            tvStatJobsDone.setText(String.valueOf(jobs));
        });
        
        homeViewModel.getRating().observe(getViewLifecycleOwner(), rat -> {
            if (rat > 0) {
                tvStatRating.setText(String.format("%.1f", rat));
                tvProfileRating.setText(String.format("⭐ %.1f", rat));
            } else {
                tvStatRating.setText("--");
                tvProfileRating.setText("⭐ --");
            }
        });
        
        homeViewModel.getRatedJobsCount().observe(getViewLifecycleOwner(), count -> {
            tvProfileReviews.setText(String.format("  (%d reviews)", count));
        });
        
        // Initialize ProfileViewModel
        profileViewModel = new androidx.lifecycle.ViewModelProvider(this).get(ProfileViewModel.class);
        
        // Observers for ProfileViewModel
        profileViewModel.getProviderProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                // Update Verified Badge
                String status = profile.getVerificationStatus();
                if ("APPROVED".equalsIgnoreCase(status)) {
                    tvVerifiedIcon.setText("✅");
                    tvVerifiedText.setText("Verified");
                    llVerifiedBadge.setBackgroundResource(R.drawable.gradient_card_green);
                } else if ("REJECTED".equalsIgnoreCase(status)) {
                    tvVerifiedIcon.setText("❌");
                    tvVerifiedText.setText("Rejected");
                    llVerifiedBadge.setBackgroundResource(R.drawable.gradient_card_amber);
                } else {
                    tvVerifiedIcon.setText("⏳");
                    tvVerifiedText.setText("Pending");
                    llVerifiedBadge.setBackgroundResource(R.drawable.gradient_card_amber);
                }
                
                // Update Bio / Experience
                String experienceStr = profile.getExperience();
                if (experienceStr != null && !experienceStr.trim().isEmpty()) {
                    tvProfileBio.setText(experienceStr);
                } else if (profile.getBio() != null && !profile.getBio().isEmpty()) {
                    tvProfileBio.setText(profile.getBio());
                }
            }
        });

        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch Profile from backend
        profileViewModel.loadProfile();
        
        // Settings & Actions
        View btnSecurity = view.findViewById(R.id.settingSecurity);
        if (btnSecurity != null) {
            btnSecurity.setOnClickListener(v -> Toast.makeText(requireContext(), "Security settings coming soon", Toast.LENGTH_SHORT).show());
        }
        
        View btnSupport = view.findViewById(R.id.settingSupport);
        if (btnSupport != null) {
            btnSupport.setOnClickListener(v -> Toast.makeText(requireContext(), "Support & Help coming soon", Toast.LENGTH_SHORT).show());
        }
        
        View btnLogout = view.findViewById(R.id.btnLogoutProvider);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                profileViewModel.logout();
                requireActivity().finish();
            });
        }
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

            // Update via ViewModel
            profileViewModel.updateProfile(nameStr, phoneStr, bioStr);

            // Update UI views directly for immediate feedback
            tvProfileName.setText(nameStr);
            tvProfilePhone.setText(phoneStr.isEmpty() ? "Not set" : phoneStr);
            tvProfileBio.setText(bioStr.isEmpty() ? "Bio not set" : bioStr);
            tvProfileInitials.setText(String.valueOf(nameStr.charAt(0)).toUpperCase());
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}