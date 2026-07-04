package com.localpro.localproandroid.models;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("_id")
    private String id;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String serviceCategory;
    private String jobDescription;
    private String distanceText;
    private String estimatedEarning;
    private String requestTime;
    private String status;
    private double customerLat;
    private double customerLon;

    public BookingRequest(String id, String customerId, String customerName, String customerPhone,
                          String serviceCategory, String jobDescription, String distanceText,
                          String estimatedEarning, String requestTime, String status,
                          double customerLat, double customerLon) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.serviceCategory = serviceCategory;
        this.jobDescription = jobDescription;
        this.distanceText = distanceText;
        this.estimatedEarning = estimatedEarning;
        this.requestTime = requestTime;
        this.status = status;
        this.customerLat = customerLat;
        this.customerLon = customerLon;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone != null ? customerPhone : ""; }
    public String getServiceCategory() { return serviceCategory; }
    public String getJobDescription() { return jobDescription; }
    public String getDistanceText() { return distanceText; }
    public String getEstimatedEarning() { return estimatedEarning; }
    public String getRequestTime() { return requestTime; }
    public String getStatus() { return status != null ? status : "active"; }
    public double getCustomerLat() { return customerLat; }
    public double getCustomerLon() { return customerLon; }

    public String getInitial() {
        if (customerName != null && !customerName.isEmpty()) {
            return String.valueOf(customerName.charAt(0)).toUpperCase();
        }
        return "C";
    }
}
