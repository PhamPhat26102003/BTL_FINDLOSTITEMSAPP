package com.example.findlostitemsapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post implements Serializable {
    private String postId;
    private String userId;
    private String title;
    private String description;
    private String itemCategory;
    private String location;
    private String lostDate;
    private String postDate;
    private List<String> imageUrls;
    private boolean isFound;
    private String contactInfo;
    private String tag;
    private int viewCount;

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
        result.put("viewCount", viewCount);
        return result;
    }

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
        post.setImageUrls(map.get("imageUrls") != null ? (List<String>) map.get("imageUrls") : new ArrayList<>());
        post.setIsFound(map.get("isFound") != null ? (Boolean) map.get("isFound") : false);
        post.setContactInfo((String) map.get("contactInfo"));
        post.setTag((String) map.get("tag"));
        post.setViewCount(map.get("viewCount") != null ? ((Long) map.get("viewCount")).intValue() : 0);
        return post;
    }

    public Post() {
    }

    public Post(String postId, String userId, String title, String description, String itemCategory, String location, String lostDate, String postDate, List<String> imageUrls, boolean isFound, String contactInfo, String tag, int viewCount) {
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
        this.viewCount = viewCount;
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}