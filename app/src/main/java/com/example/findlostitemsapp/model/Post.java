package com.example.findlostitemsapp.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {
    private String postId;
    private String userId;
    private String title;
    private String description;
    private String itemCategory; // Danh mục đồ vật
    private String location; // Địa điểm mất/địa điểm nhặt được
    private String lostDate; // Ngày mất
    private String postDate; // Ngày đăng bài
    private List<String> imageUrls; // Danh sách URL hình ảnh
    private boolean isFound; // Đã tìm thấy chưa
    private String contactInfo;
    private String tag;

    // Thêm phương thức để chuyển đổi thành Map cho Firebase
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("userId", userId);
        result.put("title", title);
        result.put("description", description);
        result.put("itemCategory", itemCategory);
        result.put("location", location);
        result.put("lostDate", lostDate);
        result.put("postDate", postDate);
        result.put("imageUrls", imageUrls);
        result.put("isFound", isFound);
        result.put("contactInfo", contactInfo);
        result.put("tag", tag);
        return result;
    }

    // hàm formmap lay du lieu
    public static Post fromMap(Map<String, Object> map) {
        Post post = new Post();
        post.setPostId((String) map.get("postId"));
        post.setUserId((String) map.get("userId"));
        post.setTitle((String) map.get("title"));
        post.setDescription((String) map.get("description"));
        post.setItemCategory((String) map.get("itemCategory"));
        post.setLocation((String) map.get("location"));
        post.setLostDate((String) map.get("lostDate"));
        post.setPostDate(map.get("postDate") != null ? (String) map.get("postDate") : "");
        post.setImageUrls((List<String>) map.get("imageUrls"));
        post.setIsFound((Boolean) map.get("isFound"));
        post.setContactInfo((String) map.get("contactInfo"));
        post.setTag((String) map.get("tag"));
        return post;
    }



    // Constructors
    public Post() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public Post(String postId, String userId, String title, String description, String itemCategory, String location, String lostDate, String postDate, List<String> imageUrls, boolean isFound, String contactInfo, String tag) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.itemCategory = itemCategory;
        this.location = location;
        this.lostDate = lostDate;
        this.postDate = postDate;
        this.imageUrls = imageUrls;
        this.isFound = isFound;
        this.contactInfo = contactInfo;
        this.tag = tag;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLostDate() {
        return lostDate;
    }

    public void setLostDate(String lostDate) {
        this.lostDate = lostDate;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public boolean getIsFound() {
        return isFound;
    }

    public void setIsFound(boolean found) {
        this.isFound = found;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}