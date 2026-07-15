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

public class RecentTransactionsAdapter extends RecyclerView.Adapter<RecentTransactionsAdapter.ViewHolder> {

    private List<BookingRequest> transactions = new ArrayList<>();

    public void setTransactions(List<BookingRequest> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest request = transactions.get(position);

        holder.tvTransactionTitle.setText("Payment Received");
        holder.tvTransactionAmount.setText(String.format("+LKR %.0f", request.getEarning()));
        
        String dateString = request.getRequestTime();
        if (dateString != null && !dateString.isEmpty()) {
            try {
                if (dateString.endsWith("+00:00")) {
                    dateString = dateString.substring(0, dateString.length() - 6) + "Z";
                }
                SimpleDateFormat sdfIn;
                if (dateString.contains(".")) {
                    sdfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                } else {
                    sdfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                }
                Date date = sdfIn.parse(dateString);
                SimpleDateFormat sdfOut = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                dateString = sdfOut.format(date);
            } catch (ParseException e) {
                dateString = "Unknown Date";
            }
        } else {
            dateString = "Unknown Date";
        }

        String customerName = request.getCustomerName() != null ? request.getCustomerName() : "Unknown";
        holder.tvTransactionSubtitle.setText(dateString + " • " + customerName);

        String paymentStatus = request.getPaymentStatus();
        if ("paid".equalsIgnoreCase(paymentStatus)) {
            holder.tvTransactionStatus.setText("Paid");
            holder.tvTransactionStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.lp_green));
        } else {
            holder.tvTransactionStatus.setText("Pending");
            holder.tvTransactionStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.lp_amber));
        }
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionTitle, tvTransactionSubtitle, tvTransactionAmount, tvTransactionStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvTransactionTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvTransactionSubtitle = itemView.findViewById(R.id.tvTransactionSubtitle);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
            tvTransactionStatus = itemView.findViewById(R.id.tvTransactionStatus);
        }
    }
}
