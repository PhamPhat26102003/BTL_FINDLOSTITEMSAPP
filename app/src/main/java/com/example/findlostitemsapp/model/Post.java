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
    private Date lostDate; // Ngày mất
    private Date postDate; // Ngày đăng bài
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
        result.put("lostDate", lostDate.getTime()); // Lưu dưới dạng timestamp
        result.put("postDate", postDate.getTime());
        result.put("imageUrls", imageUrls);
        result.put("isFound", isFound);
        result.put("contactInfo", contactInfo);
        result.put("tag", tag);
        return result;
    }


    // Constructors
    public Post() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public Post(String postId, String userId, String title, String description, String itemCategory, String location, Date lostDate, Date postDate, List<String> imageUrls, boolean isFound, String contactInfo, String tag) {
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

    public Date getLostDate() {
        return lostDate;
    }

    public void setLostDate(Date lostDate) {
        this.lostDate = lostDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
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