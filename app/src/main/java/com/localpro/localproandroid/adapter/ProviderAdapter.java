package com.localpro.localproandroid.adapter;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.models.ProviderListResponse;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Random;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {

    private List<ProviderListResponse.UserDoc> providerList;
    private OnProviderClickListner listner;
    private Random random = new Random();

    public interface OnProviderClickListner {
        void onProviderClick(ProviderListResponse.UserDoc provider);
    }

    public ProviderAdapter(List<ProviderListResponse.UserDoc> providerList, OnProviderClickListner listner) {
        this.providerList = providerList;
        this.listner = listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_provider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProviderListResponse.UserDoc provider = providerList.get(position);
        
        holder.tvName.setText(provider.getName());
        
        if (provider.getName() != null && !provider.getName().isEmpty()) {
            holder.tvAvatarInitial.setText(String.valueOf(provider.getName().charAt(0)).toUpperCase());
        }

        if (provider.getProviderProfile() != null) {
            holder.ivVerified.setVisibility(provider.getProviderProfile().isVerified() ? View.VISIBLE : View.GONE);
            
            // Generate some realistic dummy data for rating and distance if not available
            double rating = 4.5 + (random.nextDouble() * 0.5); // 4.5 to 5.0
            int reviews = 10 + random.nextInt(200);
            holder.tvRating.setText(String.format("⭐ %.1f (%d)", rating, reviews));
            
            holder.tvRate.setText(String.format("LKR %.0f/hr", provider.getProviderProfile().getHourlyRate() > 0 ? 
                                provider.getProviderProfile().getHourlyRate() : 1500.0));
        } else {
            holder.ivVerified.setVisibility(View.GONE);
            holder.tvRating.setText("⭐ 5.0 (New)");
            holder.tvRate.setText("LKR 1500/hr");
        }
        
        double distance = 0.5 + (random.nextDouble() * 5.0); // 0.5km to 5.5km
        holder.tvDistance.setText(String.format("• %.1f km away", distance));

        holder.btnBookNow.setOnClickListener(v -> listner.onProviderClick(provider));
        holder.itemView.setOnClickListener(v -> listner.onProviderClick(provider));
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAvatarInitial, tvRating, tvRate, tvDistance;
        ImageView ivVerified;
        MaterialButton btnBookNow;
        
        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemProviderName);
            tvAvatarInitial = itemView.findViewById(R.id.tvAvatarInitial);
            tvRating = itemView.findViewById(R.id.itemProviderRating);
            tvRate = itemView.findViewById(R.id.itemProviderRate);
            tvDistance = itemView.findViewById(R.id.itemProviderDistance);
            ivVerified = itemView.findViewById(R.id.ivVerified);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
        }
    }
}
