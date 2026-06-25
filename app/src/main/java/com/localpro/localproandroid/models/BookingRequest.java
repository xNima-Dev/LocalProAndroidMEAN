package com.localpro.localproandroid.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model class representing a customer booking request for a provider.
 * This is a local UI model used to populate the booking requests list
 * in the Provider Dashboard.
 */
public class BookingRequest {
    @SerializedName("_id")
    private String id;
    private String customerId;
    private String customerName;
    private String serviceCategory;
    private String jobDescription;
    private String distanceText;
    private String estimatedEarning;
    private String requestTime;
    private double customerLat;
    private double customerLon;

    public BookingRequest(String id, String customerId, String customerName, String serviceCategory,
                          String jobDescription, String distanceText,
                          String estimatedEarning, String requestTime,
                          double customerLat, double customerLon) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.serviceCategory = serviceCategory;
        this.jobDescription = jobDescription;
        this.distanceText = distanceText;
        this.estimatedEarning = estimatedEarning;
        this.requestTime = requestTime;
        this.customerLat = customerLat;
        this.customerLon = customerLon;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getServiceCategory() { return serviceCategory; }
    public String getJobDescription() { return jobDescription; }
    public String getDistanceText() { return distanceText; }
    public String getEstimatedEarning() { return estimatedEarning; }
    public String getRequestTime() { return requestTime; }
    public double getCustomerLat() { return customerLat; }
    public double getCustomerLon() { return customerLon; }

    /**
     * Returns the first letter of the customer's name for avatar display.
     */
    public String getInitial() {
        if (customerName != null && !customerName.isEmpty()) {
            return String.valueOf(customerName.charAt(0)).toUpperCase();
        }
        return "C";
    }
}
