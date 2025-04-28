package com.example.findlostitemsapp.pages.search;

import com.example.findlostitemsapp.model.Post;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class PostFilterUtils {

    private String removeAccents(String s) {
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
    public List<Post> filterPosts(
            List<Post> allPosts,
            String keyword,
            String selectedTag,
            String selectedCategory,
            String selectedLocation,
            String selectedTime
    ) {
        List<Post> filteredPosts = new ArrayList<>();

        for (Post post : allPosts) {
            boolean matches = true;

            // Lọc theo từ khóa (xóa dấu, chữ thường)
            if (keyword != null && !keyword.isEmpty()) {
                String lowerKeyword = removeAccents(keyword.trim().toLowerCase());

                String title = removeAccents(post.getTitle().toLowerCase());
                String description = removeAccents(post.getDescription().toLowerCase());

                if (!(title.contains(lowerKeyword) || description.contains(lowerKeyword))) {
                    matches = false;
                }
            }

            // Lọc theo loại bài viết (tag)
            if (selectedTag != null &&
                    !selectedTag.equals("Tất cả") &&
                    !selectedTag.equals("Loại bài viết")) {
                if (!selectedTag.equalsIgnoreCase(post.getTag())) {
                    matches = false;
                }
            }

            // Lọc theo danh mục (category)
            if (selectedCategory != null &&
                    !selectedCategory.equals("Tất cả") &&
                    !selectedCategory.equals("Danh mục")) {
                if (!selectedCategory.equalsIgnoreCase(post.getItemCategory())) {
                    matches = false;
                }
            }

            // Lọc theo địa điểm (location)
            if (selectedLocation != null &&
                    !selectedLocation.equals("Tất cả") &&
                    !selectedLocation.equals("Tỉnh/Thành phố")) {
                if (!post.getLocation().toLowerCase().contains(selectedLocation.toLowerCase())) {
                    matches = false;
                }
            }

            // Lọc theo thời gian
            if (selectedTime != null &&
                    !selectedTime.equals("Tất cả") &&
                    !selectedTime.equals("Thời gian")) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date postDate = sdf.parse(post.getPostDate());
                    Calendar now = Calendar.getInstance();
                    Calendar postCal = Calendar.getInstance();
                    postCal.setTime(postDate);

                    switch (selectedTime) {
                        case "Hôm nay":
                            if (!(now.get(Calendar.YEAR) == postCal.get(Calendar.YEAR) &&
                                    now.get(Calendar.DAY_OF_YEAR) == postCal.get(Calendar.DAY_OF_YEAR))) {
                                matches = false;
                            }
                            break;

                        case "Tuần này":
                            if (!(now.get(Calendar.YEAR) == postCal.get(Calendar.YEAR) &&
                                    now.get(Calendar.WEEK_OF_YEAR) == postCal.get(Calendar.WEEK_OF_YEAR))) {
                                matches = false;
                            }
                            break;

                        case "Tháng này":
                            if (!(now.get(Calendar.YEAR) == postCal.get(Calendar.YEAR) &&
                                    now.get(Calendar.MONTH) == postCal.get(Calendar.MONTH))) {
                                matches = false;
                            }
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    matches = false;
                }
            }

            if (matches) {
                filteredPosts.add(post);
            }
        }

        return filteredPosts;
    }

}
