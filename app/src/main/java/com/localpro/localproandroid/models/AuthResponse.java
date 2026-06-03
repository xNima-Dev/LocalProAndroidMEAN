package com.localpro.localproandroid.models;

public class AuthResponse {
    private String status;
    private String token;
    private User user;

    public static  class User {
        private String id;
        private String role;

        public String getId() {
            return id;
        }
        public String getRole() {
            return role;
        }
    }
}
