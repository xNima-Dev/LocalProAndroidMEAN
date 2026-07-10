package com.localpro.localproandroid.models;

public class ProfileRequest {
    private String name;
    private String phoneNumber;
    private String bio;
    private Double hourlyRate;

    public ProfileRequest(String name, String phoneNumber, String bio, Double hourlyRate) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.hourlyRate = hourlyRate;
    }

    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBio() { return bio; }
    public Double getHourlyRate() { return hourlyRate; }
}
