// Auth Response DTO
package com.davivienda.surveyplatform.dto.auth;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthResponse {

    private String token;
    private UserInfo user;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    // Inner class for user info
    public static class UserInfo {
        private UUID id;
        private String name;
        private String email;
        private String company;
        private LocalDateTime createdAt;

        // Constructors
        public UserInfo() {}

        public UserInfo(UUID id, String name, String email, String company, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.company = company;
            this.createdAt = createdAt;
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}