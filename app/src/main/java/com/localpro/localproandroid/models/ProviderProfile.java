package com.localpro.localproandroid.models;

public class ProviderProfile {
    private String serviceCategory;
    private String experience;
    private double hourlyRate;
    private String bio;
    private String verificationStatus;
    private boolean isVerified;
    private boolean isOnline;
    private int experienceYears;

    public String getServiceCategory() {
        return serviceCategory;
    }

    public String getExperience() {
        return experience;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public String getBio() {
        return bio;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public int getExperienceYears() {
        return experienceYears;
    }
}
