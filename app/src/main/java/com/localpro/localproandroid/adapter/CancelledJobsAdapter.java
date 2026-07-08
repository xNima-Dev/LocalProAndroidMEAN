package com.localpro.localproandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.models.BookingRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CancelledJobsAdapter extends RecyclerView.Adapter<CancelledJobsAdapter.ViewHolder> {

    private final List<BookingRequest> cancelledJobsList = new ArrayList<>();

    public void setJobsList(List<BookingRequest> jobs) {
        this.cancelledJobsList.clear();
        if (jobs != null) {
            this.cancelledJobsList.addAll(jobs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cancelled_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest job = cancelledJobsList.get(position);
        holder.tvServiceCategory.setText(job.getServiceCategory());
        holder.tvCustomerInitial.setText(job.getInitial());
        holder.tvCustomerName.setText(job.getCustomerName());
        holder.tvDate.setText(formatDateString(job.getRequestTime()));
    }

    @Override
    public int getItemCount() {
        return cancelledJobsList.size();
    }

    private String formatDateString(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "N/A";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceCategory, tvCustomerInitial, tvCustomerName, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceCategory = itemView.findViewById(R.id.tvServiceCategory);
            tvCustomerInitial = itemView.findViewById(R.id.tvCustomerInitial);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}