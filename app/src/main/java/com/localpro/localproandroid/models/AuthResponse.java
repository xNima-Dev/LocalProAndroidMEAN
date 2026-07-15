package com.localpro.localproandroid.models;

public class AuthResponse {
    private String status;
    private String token;
    private User user;

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public static  class User {
        private String id;
        private String name;
        private String email;
        private String role;
        private String phoneNumber;
        private ProviderProfile providerProfile;

        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getEmail() {
            return email;
        }
        public String getRole() {
            return role;
        }
        public String getPhoneNumber() {
            return phoneNumber;
        }
        public ProviderProfile getProviderProfile() {
            return providerProfile;
        }
    }
}
