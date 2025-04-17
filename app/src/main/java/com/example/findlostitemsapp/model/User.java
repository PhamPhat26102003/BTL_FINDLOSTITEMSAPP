package com.example.findlostitemsapp.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String profileImageUrl;
    private String address;
    private int postsCount; // Số bài đăng đã tạo

    // Thêm phương thức để chuyển đổi thành Map cho Firebase
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("firstname", firstName);
        result.put("lastname", lastName);
        result.put("email", email);
        result.put("password", password);
        result.put("phoneNumber", phoneNumber);
        result.put("profileImageUrl", profileImageUrl);
        result.put("address", address);
        result.put("postsCount", postsCount);
        return result;
    }

    // Constructors
    public User() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public User(String userId, int postsCount, String address, String phoneNumber, String profileImageUrl, String email,String lastName, String firstName) {
        this.userId = userId;
        this.postsCount = postsCount;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }
}
