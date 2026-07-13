package com.localpro.localproandroid.models;

public class CreateBookingRequest {
    private String providerId;
    private String serviceCategory;
    private String jobDescription;
    private String distanceText;
    private double estimatedEarning;
    private double customerLat;
    private double customerLon;

    public CreateBookingRequest(String providerId, String serviceCategory, String jobDescription,
                                String distanceText, double estimatedEarning, double customerLat, double customerLon) {
        this.providerId = providerId;
        this.serviceCategory = serviceCategory;
        this.jobDescription = jobDescription;
        this.distanceText = distanceText;
        this.estimatedEarning = estimatedEarning;
        this.customerLat = customerLat;
        this.customerLon = customerLon;
    }

    public String getProviderId() { return providerId; }
    public String getServiceCategory() { return serviceCategory; }
    public String getJobDescription() { return jobDescription; }
    public String getDistanceText() { return distanceText; }
    public double getEstimatedEarning() { return estimatedEarning; }
    public double getCustomerLat() { return customerLat; }
    public double getCustomerLon() { return customerLon; }
}
