package com.example.findlostitemsapp.pages.post;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.home.Home;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.findlostitemsapp.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PostDetailActivity extends AppCompatActivity {

    private TextView tvBreadcrumbTitle, textTitle,
            textImportantTag, tvDate, tvViewCount,
            tvAuthor, tvContent, tvLocation, tvPhone;
    private ImageView imgPost;
    private ChipGroup chipGroup;
    private TextView tvClaim,tvBreadcrumbPrefix;
    private DatabaseReference databaseReference;
    private Post postData;

    String[] tags = {"Ví tiền", "Giấy tờ", "Nhặt được giấy tờ",
            "Nhặt được CCCD", "Nhặt được điện thoại", "Nhặt được ví",
            "Túi xách nữ", "Rơi chìa khóa", "Tìm chia khóa", "Rơi ví",
            "Tìm pet"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        InitUI();

        addChipGroup();

        tvBreadcrumbPrefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHomeActivity();
            }
        });

        // xử li đẩy dữ liệu từ home lên màn chi tiết
        postData = (Post) getIntent().getSerializableExtra("post_data");

        if (postData != null) {
            loadDataPostDetail(postData.getPostId()); // Lấy dữ liệu chi tiết từ Firebase
        } else {
            Log.e("PostDetailActivity", "Không nhận được đối tượng Post");
        }



    }

    private void loadDataPostDetail(String postId) {
        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        // Lấy dữ liệu từ Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Post post = dataSnapshot.getValue(Post.class); // Chuyển dữ liệu từ Firebase thành đối tượng Post

                    if (post != null) {
                        // Cập nhật UI với dữ liệu bài viết
                        updateUI(post);
                    } else {
                        Log.e("PostDetailActivity", "Không tìm thấy bài viết.");
                    }
                } else {
                    Log.e("PostDetailActivity", "Dữ liệu bài viết không tồn tại.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostDetailActivity", "Lỗi khi lấy dữ liệu từ Firebase: " + error.getMessage());
            }
        });
    }

    private void updateUI(Post post) {
        tvBreadcrumbTitle.setText(post.getTitle()); // Cập nhật Breadcrumb title
        textTitle.setText(post.getTitle()); // Tiêu đề bài viết
        textImportantTag.setText("Tin quan trọng"); // Tag bài viết
        tvDate.setText("📅 " + post.getPostDate()); // Ngày đăng
        tvViewCount.setText("👁 " +post.getViewCount()+ " lượt xem"); // Lượt xem
        tvAuthor.setText("👤 Quản trị viên"); // Tên người đăng
        tvContent.setText(post.getDescription()); // Nội dung bài viết
        tvLocation.setText(post.getLocation()); // Địa điểm
        tvPhone.setText(post.getContactInfo()); // Số điện thoại
        tvClaim.setText(post.getTag());

        // Nếu có ảnh bài viết, tải ảnh lên ImageView
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
//            Glide.with(this).load(post.getImageUrls()).into(imgPost);
            Glide.with(this)
                    .load(post.getImageUrls().get(0))
                    .placeholder(R.drawable.minhhoa)
                    .error(R.drawable.minhhoa)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgPost);// Sử dụng thư viện Glide để tải ảnh
        }

        // Cập nhật lượt xem bài viết trong Firebase (tăng lượt xem)
        updateViewCount(post);
    }

    private void updateViewCount(Post post) {
        int currentViewCount = post.getViewCount() + 1; // Tăng lượt xem lên 1
        databaseReference.child("viewCount").setValue(currentViewCount); // Cập nhật lại Firebase
    }

    private void InitUI() {
        tvBreadcrumbTitle = findViewById(R.id.tvBreadcrumbTitle);
        tvBreadcrumbPrefix = findViewById(R.id.tvBreadcrumbPrefix);
        textTitle = findViewById(R.id.textTitle);
        textImportantTag = findViewById(R.id.textImportantTag);
        tvDate = findViewById(R.id.tvDate);
        tvViewCount = findViewById(R.id.tvViewCount);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvContent = findViewById(R.id.tvContent);
        tvLocation = findViewById(R.id.tvLocation);
        tvPhone = findViewById(R.id.tvPhone);
        imgPost = findViewById(R.id.imgPost);
        chipGroup = findViewById(R.id.chipGroup);
        tvClaim = findViewById(R.id.tvClaim);
    }

    private void backHomeActivity() {
        Intent intent = new Intent(PostDetailActivity.this, Home.class);
        startActivity(intent);
    }

    private void addChipGroup() {
        for (String tag : tags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setTextColor(Color.BLACK);
            chip.setChipBackgroundColorResource(R.color.chip_background_color); // Tùy chọn màu
            chip.setClickable(true);
            chip.setCheckable(false); // Nếu bạn muốn người dùng chọn/bỏ chọn thì để true

            chipGroup.addView(chip);
        }
    }
}