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

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying booking requests in the Provider Dashboard.
 * Each card shows customer info, service category, estimated earning,
 * and Accept / Decline action buttons.
 */
public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.ViewHolder> {

    public interface OnBookingActionListener {
        void onAccept(BookingRequest request, int position);
        void onDecline(BookingRequest request, int position);
    }

    private final List<BookingRequest> requests;
    private final OnBookingActionListener listener;
    private int lastAnimatedPosition = -1;

    public BookingRequestAdapter(OnBookingActionListener listener) {
        this.requests = new ArrayList<>();
        this.listener = listener;
    }

    public void setRequests(List<BookingRequest> newRequests) {
        requests.clear();
        if (newRequests != null) {
            requests.addAll(newRequests);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < requests.size()) {
            requests.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, requests.size());
        }
    }

    public int getRequestCount() {
        return requests.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_request, parent, false);
        return new ViewHolder(view);
    }

    @android.annotation.SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest request = requests.get(position);

        // Set data
        holder.tvCustomerInitial.setText(request.getInitial());
        holder.tvCustomerName.setText(request.getCustomerName());
        holder.tvCustomerDistance.setText(request.getDistanceText());
        holder.tvRequestTime.setText(request.getRequestTime());
        holder.tvServiceCategory.setText(request.getServiceCategory());
        holder.tvEstimatedEarning.setText(request.getEstimatedEarning());
        holder.tvJobDescription.setText(request.getJobDescription());

        // Accept action
        holder.btnAccept.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID && listener != null) {
                listener.onAccept(requests.get(pos), pos);
            }
        });

        // Decline action
        holder.btnDecline.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID && listener != null) {
                listener.onDecline(requests.get(pos), pos);
            }
        });

        // Staggered slide-in animation
        if (position > lastAnimatedPosition) {
            Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_right);
            anim.setStartOffset(position * 60L); // stagger delay
            holder.itemView.startAnimation(anim);
            lastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerInitial;
        TextView tvCustomerName;
        TextView tvCustomerDistance;
        TextView tvRequestTime;
        TextView tvServiceCategory;
        TextView tvEstimatedEarning;
        TextView tvJobDescription;
        View btnAccept;
        View btnDecline;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerInitial = itemView.findViewById(R.id.tvCustomerInitial);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerDistance = itemView.findViewById(R.id.tvCustomerDistance);
            tvRequestTime = itemView.findViewById(R.id.tvRequestTime);
            tvServiceCategory = itemView.findViewById(R.id.tvServiceCategory);
            tvEstimatedEarning = itemView.findViewById(R.id.tvEstimatedEarning);
            tvJobDescription = itemView.findViewById(R.id.tvJobDescription);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }
}
