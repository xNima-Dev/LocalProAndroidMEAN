package com.localpro.localproandroid.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.models.BookingRequest;

import java.util.ArrayList;
import java.util.List;

public class CompletedJobsAdapter extends RecyclerView.Adapter<CompletedJobsAdapter.CompletedJobViewHolder> {
    private List<BookingRequest> completedJobsList = new ArrayList<>();

    public void setJobsList(List<BookingRequest> jobs) {
        this.completedJobsList = jobs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CompletedJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_job, parent, false);
        return new CompletedJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedJobViewHolder holder, int position) {
        BookingRequest job = completedJobsList.get(position);

        holder.tvCustomerInitial.setText(job.getInitial());
        holder.tvCustomerName.setText(job.getCustomerName());
        holder.tvServiceCategory.setText(job.getServiceCategory());
        holder.tvDate.setText(job.getRequestTime());

        holder.tvEarning.setText(String.format("LKR %.2f", job.getEarning()));

        holder.tvRating.setText(String.format("★ %.1f", job.getRating()));

        if ("paid".equalsIgnoreCase(job.getPaymentStatus())) {
            holder.tvPaymentStatus.setText("PAID");
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvPaymentStatus.setText("UNPAID");
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return completedJobsList != null ? completedJobsList.size() : 0;
    }

    public static class CompletedJobViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerInitial, tvCustomerName, tvServiceCategory, tvDate, tvEarning, tvRating, tvPaymentStatus;

        public CompletedJobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerInitial = itemView.findViewById(R.id.tvCustomerInitial);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvServiceCategory = itemView.findViewById(R.id.tvServiceCategory);
            tvDate = itemView.findViewById(R.id.tvJobDate);
            tvEarning = itemView.findViewById(R.id.tvEarning);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
        }
    }
}