package com.localpro.localproandroid.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BookingResponse {
    public String status;
    public int results;

    @SerializedName(value = "data", alternate = {"bookings"})
    public List<BookingRequest> data;

    public List<BookingRequest> getBookings() {
        return data;
    }
}