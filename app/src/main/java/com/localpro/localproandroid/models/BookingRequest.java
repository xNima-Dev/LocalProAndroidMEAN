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

    @SerializedName("jobStatus")
    private String jobStatus;       // "pending" | "accepted" | "riding" | "arrived" | "completed"

    @SerializedName("paymentStatus")
    private String paymentStatus;   // "pending" | "paid" | "unpaid"

    @SerializedName("rating")
    private double rating;

    @SerializedName("earning")
    private double earning;

    private double customerLat;
    private double customerLon;

    public BookingRequest() {
    }

    public BookingRequest(String id, String customerId, String customerName, String customerPhone,
                          String serviceCategory, String jobDescription, String distanceText,
                          String estimatedEarning, String requestTime, String jobStatus,
                          String paymentStatus, double rating, double earning,
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
        this.jobStatus = jobStatus;
        this.paymentStatus = paymentStatus;
        this.rating = rating;
        this.earning = earning;
        this.customerLat = customerLat;
        this.customerLon = customerLon;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone != null ? customerPhone : ""; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getDistanceText() { return distanceText; }
    public void setDistanceText(String distanceText) { this.distanceText = distanceText; }

    public String getEstimatedEarning() { return estimatedEarning; }
    public void setEstimatedEarning(String estimatedEarning) { this.estimatedEarning = estimatedEarning; }

    public String getRequestTime() { return requestTime; }
    public void setRequestTime(String requestTime) { this.requestTime = requestTime; }

    public String getStatus() { return jobStatus != null ? jobStatus : "pending"; }

    public String getJobStatus() { return jobStatus != null ? jobStatus : "pending"; }
    public void setJobStatus(String jobStatus) { this.jobStatus = jobStatus; }

    public String getPaymentStatus() { return paymentStatus != null ? paymentStatus : "pending"; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public double getEarning() { return earning; }
    public void setEarning(double earning) { this.earning = earning; }

    public double getCustomerLat() { return customerLat; }
    public void setCustomerLat(double customerLat) { this.customerLat = customerLat; }

    public double getCustomerLon() { return customerLon; }
    public void setCustomerLon(double customerLon) { this.customerLon = customerLon; }

    public String getInitial() {
        if (customerName != null && !customerName.isEmpty()) {
            return String.valueOf(customerName.charAt(0)).toUpperCase();
        }
        return "C";
    }
}