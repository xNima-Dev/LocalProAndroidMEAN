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

import java.util.List;

public class CustomerBookingAdapter extends RecyclerView.Adapter<CustomerBookingAdapter.ViewHolder> {

    private List<BookingRequest> bookingList;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(BookingRequest booking);
    }

    public CustomerBookingAdapter(List<BookingRequest> bookingList, OnBookingClickListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    public void setBookings(List<BookingRequest> newBookings) {
        this.bookingList = newBookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest booking = bookingList.get(position);

        if (booking.getProvider() != null) {
            String providerName = booking.getProvider().getName();
            holder.tvProviderName.setText(providerName != null ? providerName : "Unknown Provider");
            if (providerName != null && !providerName.isEmpty()) {
                holder.tvProviderInitial.setText(String.valueOf(providerName.charAt(0)).toUpperCase());
            }
        } else {
            holder.tvProviderName.setText("Provider Assigned");
            holder.tvProviderInitial.setText("P");
        }

        String category = booking.getServiceCategory();
        holder.tvCategory.setText(category != null ? category.toUpperCase().replace("-", " ") + " Specialist" : "Service");

        // Format Date
        try {
            if (booking.getRequestTime() != null) {
                holder.tvDate.setText("Requested on " + booking.getRequestTime().substring(0, 10));
            } else {
                holder.tvDate.setText("Requested recently");
            }
        } catch (Exception e) {
            holder.tvDate.setText("Requested recently");
        }

        // Set status and color
        String status = booking.getStatus() != null ? booking.getStatus().toLowerCase() : "pending";
        holder.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        
        switch (status) {
            case "pending":
                holder.tvStatus.setTextColor(Color.parseColor("#FBBF24")); // Yellow
                break;
            case "accepted":
            case "riding":
            case "arrived":
            case "started":
                holder.tvStatus.setTextColor(Color.parseColor("#00D2FF")); // Cyan
                break;
            case "completed":
            case "paid":
                holder.tvStatus.setTextColor(Color.parseColor("#10B981")); // Green
                break;
            case "declined":
            case "cancelled":
                holder.tvStatus.setTextColor(Color.parseColor("#F43F5E")); // Red
                break;
            default:
                holder.tvStatus.setTextColor(Color.parseColor("#94A3B8")); // Slate
                break;
        }

        holder.itemView.setOnClickListener(v -> listener.onBookingClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProviderName, tvCategory, tvStatus, tvDate, tvProviderInitial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProviderName = itemView.findViewById(R.id.tvBookingProviderName);
            tvCategory = itemView.findViewById(R.id.tvBookingCategory);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvDate = itemView.findViewById(R.id.tvBookingDate);
            tvProviderInitial = itemView.findViewById(R.id.tvProviderInitial);
        }
    }
}
