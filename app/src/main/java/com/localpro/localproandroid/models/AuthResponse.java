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
        private String role;
        private Object providerProfile;

        public String getId() {
            return id;
        }
        public String getRole() {
            return role;
        }
        public Object getProviderProfile() {
            return providerProfile;
        }
    }
}
