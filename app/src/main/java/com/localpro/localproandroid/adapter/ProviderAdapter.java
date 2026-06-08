package com.localpro.localproandroid.adapter;

import android.view.ViewGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.models.ProviderListResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {

    private List<ProviderListResponse.UserDoc> providerList;
    private OnProviderClickListner listner;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int viewType) {
        ProviderListResponse.UserDoc provider = providerList.get(viewType);
        holder.tvName.setText(provider.getName());

        if (provider.getProviderProfile() != null) {
            holder.tvExp.setText("Exp: " + provider.getProviderProfile().getExperienceYears() + " Years");
            holder.tvStatus.setText(provider.getProviderProfile().isOnline() ? "ONLINE" : "OFFLINE");
            holder.tvStatus.setTextColor(provider.getProviderProfile().isOnline() ? 0xFF10B981 : 0xFF94A3B8);
            holder.tvVerified.setVisibility(provider.getProviderProfile().isVerified() ? View.VISIBLE : View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listner.onProviderClick(provider));
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvExp, tvVerified;
        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemProviderName);
            tvStatus = itemView.findViewById(R.id.itemProviderStatus);
            tvExp = itemView.findViewById(R.id.itemProviderExp);
            tvVerified = itemView.findViewById(R.id.itemProviderVerified);
        }
    }
}
