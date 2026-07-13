package com.localpro.localproandroid.models;

import com.google.gson.annotations.SerializedName;

public class CreateBookingResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("booking")
    private BookingRequest booking;

    public String getStatus() { return status; }
    public BookingRequest getBooking() { return booking; }
}
