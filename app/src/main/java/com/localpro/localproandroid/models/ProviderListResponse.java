package com.localpro.localproandroid.models;

import java.util.List;

public class ProviderListResponse {
    private String status;
    private int results;
    private List<UserDoc> providers;

    public String getStatus() { return status; }
    public int getResults() { return results; }
    public List<UserDoc> getProviders() { return providers; }

    public static class UserDoc {
        private String _id;
        private String name;
        private String email;
        private String role;
        private String phoneNumber;
        private LocationModel location;

        private ProviderProfile providerProfile;

        public String get_id() { return _id; }
        // Alias for consistency
        public String getId() { return _id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getPhoneNumber() { return phoneNumber; }
        public LocationModel getLocation() { return location; }

        // Getter
        public ProviderProfile getProviderProfile() { return providerProfile; }
    }

    public static class ProviderProfile {
        private String category;
        private String serviceCategory;
        private boolean isVerified;
        private boolean isOnline;
        private String profileImage;
        private int experienceYears;
        private double hourlyRate;
        private String bio;

        public String getCategory() { return category != null ? category : serviceCategory; }
        public String getServiceCategory() { return serviceCategory; }
        public boolean isVerified() { return isVerified; }
        public boolean isOnline() { return isOnline; }
        public String getProfileImage() { return profileImage; }
        public int getExperienceYears() { return experienceYears; }
        public double getHourlyRate() { return hourlyRate; }
        public String getBio() { return bio; }
    }

    public static class LocationModel {
        private String type;
        private List<Double> coordinates; // [Longitude, Latitude]

        public String getType() { return type; }
        public List<Double> getCoordinates() { return coordinates; }
    }
}