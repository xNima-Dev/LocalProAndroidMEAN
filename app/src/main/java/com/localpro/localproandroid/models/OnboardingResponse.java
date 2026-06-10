package com.localpro.localproandroid.models;

import com.google.gson.annotations.SerializedName;

public class OnboardingResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}