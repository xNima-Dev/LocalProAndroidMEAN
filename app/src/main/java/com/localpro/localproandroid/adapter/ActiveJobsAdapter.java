package com.localpro.localproandroid.adapter;

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

    public void removeItem(int position) {
        if (position >= 0 && position < activeJobs.size()) {
            activeJobs.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, activeJobs.size());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_job, parent, false);
        return new ViewHolder(view);
    }

    @android.annotation.SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest request = activeJobs.get(position);

        holder.tvCustomerInitial.setText(request.getInitial());
        holder.tvCustomerName.setText(request.getCustomerName());
        holder.tvCustomerDistance.setText(request.getDistanceText());
        holder.tvServiceCategory.setText(request.getServiceCategory());
        holder.tvEstimatedEarning.setText(String.valueOf(request.getEstimatedEarning()));
        holder.tvJobDescription.setText(request.getJobDescription());
        
        // Format the request time
        holder.tvRequestTime.setText(formatDateString(request.getRequestTime()));

        // Cancel action
        holder.btnCancel.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID && listener != null) {
                listener.onCancelJob(activeJobs.get(pos), pos);
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
            // Backend returns ISO format: "2026-07-03T10:00:00.000Z"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            
            // Format to: "03 Jul 2026, 10:00 AM"
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // fallback to original string
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerInitial;
        TextView tvCustomerName;
        TextView tvCustomerDistance;
        TextView tvRequestTime;
        TextView tvServiceCategory;
        TextView tvEstimatedEarning;
        TextView tvJobDescription;
        View btnCancel;

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
        }
    }
}
