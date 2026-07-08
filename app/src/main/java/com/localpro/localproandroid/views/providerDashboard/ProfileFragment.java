package com.localpro.localproandroid.views.providerDashboard;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localpro.localproandroid.R;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

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

        // Initials
        TextView tvProfileInitials = view.findViewById(R.id.tvProfileInitials);
        if (name != null && !name.trim().isEmpty()) {
            tvProfileInitials.setText(String.valueOf(name.trim().charAt(0)).toUpperCase());
        } else {
            tvProfileInitials.setText("P");
        }

        // Name
        TextView tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileName.setText(name);

        // Email
        TextView tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileEmail.setText(email);

        // Category from SharedPreferences (saved during onboarding)
        String category = prefs.getString("provider_category", null);
        TextView tvProfileCategory = view.findViewById(R.id.tvProfileCategory);
        if (category != null && !category.trim().isEmpty()) {
            tvProfileCategory.setText("🔧 " + category);
        }

        // Phone
        String phone = prefs.getString("provider_phone", null);
        TextView tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        if (phone != null && !phone.trim().isEmpty()) {
            tvProfilePhone.setText(phone);
        } else {
            tvProfilePhone.setText("Not set");
        }

        // Stats: member since (rough estimate from user_id saved time)
        TextView tvStatMemberSince = view.findViewById(R.id.tvStatMemberSince);
        tvStatMemberSince.setText("2026");

        // Jobs done and rating from prefs if saved, otherwise show defaults
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
        TextView tvProfileBio = view.findViewById(R.id.tvProfileBio);
        String bio = prefs.getString("provider_bio", null);
        if (bio != null && !bio.trim().isEmpty()) {
            tvProfileBio.setText(bio);
        }
    }
}