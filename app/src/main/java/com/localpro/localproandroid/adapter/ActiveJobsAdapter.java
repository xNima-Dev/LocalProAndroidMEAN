package com.localpro.localproandroid.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class ActiveJobsAdapter extends RecyclerView.Adapter<ActiveJobsAdapter.ViewHolder> {

    public interface OnActiveJobActionListener {
        void onCancelJob(BookingRequest request, int position);
        void onStartJob(BookingRequest request);
    }

    private final List<BookingRequest> activeJobs;
    private final OnActiveJobActionListener listener;
    private int lastAnimatedPosition = -1;

    public ActiveJobsAdapter(OnActiveJobActionListener listener) {
        this.activeJobs = new ArrayList<>();
        this.listener = listener;
    }

    public void setJobs(List<BookingRequest> jobs) {
        activeJobs.clear();
        if (jobs != null) {
            activeJobs.addAll(jobs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BookingRequest request = activeJobs.get(position);

        holder.tvCustomerInitial.setText(request.getInitial());
        holder.tvCustomerName.setText(request.getCustomerName());
        holder.tvCustomerDistance.setText(request.getDistanceText());
        holder.tvServiceCategory.setText(request.getServiceCategory());
        holder.tvEstimatedEarning.setText(String.valueOf(request.getEstimatedEarning()));
        holder.tvJobDescription.setText(request.getJobDescription());

        holder.tvRequestTime.setText(formatDateString(request.getRequestTime()));

        holder.btnCancel.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onCancelJob(activeJobs.get(pos), pos);
            }
        });

        holder.btnStart.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onStartJob(activeJobs.get(pos));
            }
        });

        if (position > lastAnimatedPosition) {
            Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_right);
            anim.setStartOffset(position * 60L);
            holder.itemView.startAnimation(anim);
            lastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return activeJobs.size();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerInitial, tvCustomerName, tvCustomerDistance, tvRequestTime,
                tvServiceCategory, tvEstimatedEarning, tvJobDescription;
        View btnCancel, btnStart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerInitial = itemView.findViewById(R.id.tvCustomerInitial);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerDistance = itemView.findViewById(R.id.tvCustomerDistance);
            tvRequestTime = itemView.findViewById(R.id.tvRequestTime);
            tvServiceCategory = itemView.findViewById(R.id.tvServiceCategory);
            tvEstimatedEarning = itemView.findViewById(R.id.tvEstimatedEarning);
            tvJobDescription = itemView.findViewById(R.id.tvJobDescription);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}