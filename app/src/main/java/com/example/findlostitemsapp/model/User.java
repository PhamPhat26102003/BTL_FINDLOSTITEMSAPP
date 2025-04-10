package com.example.findlostitemsapp.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private int postsCount; // Số bài đăng đã tạo

    // Thêm phương thức để chuyển đổi thành Map cho Firebase
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("email", email);
        result.put("phoneNumber", phoneNumber);
        result.put("profileImageUrl", profileImageUrl);
        result.put("postsCount", postsCount);
        return result;
    }

    // Constructors
    public User() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public User(String userId, String username, String email, String phoneNumber) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.postsCount = 0;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }
}
