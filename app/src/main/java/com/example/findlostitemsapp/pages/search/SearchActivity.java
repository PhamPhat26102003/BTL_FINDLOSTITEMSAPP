package com.example.findlostitemsapp.pages.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.home.PostAdapter;
import com.example.findlostitemsapp.pages.notification.NotificationActivity;
import com.example.findlostitemsapp.pages.post.*;
import com.example.findlostitemsapp.pages.post.PostDetailActivity;
import com.example.findlostitemsapp.pages.post.PostsActivity;
import com.example.findlostitemsapp.pages.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity implements PostAdapter.OnPostClickListener{

    private BottomNavigationView bottomNav;
    private Spinner spinnerLoaiBaiViet, spinnerDanhMuc, spinnerThoiGian, spinnerTinhThanh;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private Button btnTimKiem, btnTatCa;
    private EditText edtKeyword;
    private TextView tvKetQuaSoLuong;
    private List<com.example.findlostitemsapp.model.Post> postList;
    private List<com.example.findlostitemsapp.model.Post> originalPostList = new ArrayList<>();
    private CustomSpinnerAdapter adapterLoaiBaiViet, adapterDanhMuc, adapterThoiGian, adapterTinhThanh;
    private List<String> listLoaiBaiViet = new ArrayList<>();
    private List<String> listDanhMuc = new ArrayList<>();
    private List<String> listThoiGian = new ArrayList<>();
    private List<String> listTinhThanh = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUi();

        bottomNavigationBarAction();

        //loda data spinner
        adapterLoaiBaiViet = new CustomSpinnerAdapter(this, listLoaiBaiViet, spinnerLoaiBaiViet);
        spinnerLoaiBaiViet.setAdapter(adapterLoaiBaiViet);

        adapterDanhMuc = new CustomSpinnerAdapter(this, listDanhMuc, spinnerDanhMuc);
        spinnerDanhMuc.setAdapter(adapterDanhMuc);

        adapterThoiGian = new CustomSpinnerAdapter(this, listThoiGian, spinnerThoiGian);
        spinnerThoiGian.setAdapter(adapterThoiGian);

        adapterTinhThanh = new CustomSpinnerAdapter(this, listTinhThanh, spinnerTinhThanh);
        spinnerTinhThanh.setAdapter(adapterTinhThanh);

        loadSpinnerData("tag", listLoaiBaiViet, adapterLoaiBaiViet, "Loại bài viết");
        loadSpinnerData("itemCategory", listDanhMuc, adapterDanhMuc, "Danh mục");
        loadSpinnerData("time_filters", listThoiGian, adapterThoiGian, "Thời gian");
        loadSpinnerData("location", listTinhThanh, adapterTinhThanh, "Tỉnh/Thành phố");


        // Khởi tạo RecyclerView và PostAdapter
        postList = new ArrayList<>();
        postAdapter = new PostAdapter( this, postList, (PostAdapter.OnPostClickListener) this);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(postAdapter);
        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        // Lấy dữ liệu từ Firebase
        loadPostsFromFirebase();

        // xử lí click tìm kiếm để lọc
        btnTimKiem.setOnClickListener(v -> {
            logCatSearch();
        });


        // xu li nut tat ca

        btnTatCa.setOnClickListener(v -> {
            // Xóa toàn bộ lựa chọn lọc
            edtKeyword.setText(""); // Xóa ô tìm kiếm

            spinnerLoaiBaiViet.setSelection(0);        // Chọn "Tất cả"
            spinnerDanhMuc.setSelection(0);   // Chọn "Tất cả"
            spinnerTinhThanh.setSelection(0);   // Chọn "Tất cả"
            spinnerThoiGian.setSelection(0);       // Chọn "Tất cả"

            // Hiển thị lại toàn bộ danh sách gốc
            postAdapter.updatePosts(originalPostList);

        });


    }

    private void logCatSearch() {
        String keyword = edtKeyword.getText().toString().trim();
        String selectedTag = spinnerLoaiBaiViet.getSelectedItem().toString();
        String selectedCategory = spinnerDanhMuc.getSelectedItem().toString();
        String selectedLocation = spinnerTinhThanh.getSelectedItem().toString();
        String selectedTime = spinnerThoiGian.getSelectedItem().toString();

        Log.d("DEBUG", "Từ khóa: " + keyword);
        Log.d("DEBUG", "Tag: " + selectedTag);
        Log.d("DEBUG", "Danh mục: " + selectedCategory);
        Log.d("DEBUG", "Tỉnh/Thành: " + selectedLocation);
        Log.d("DEBUG", "Thời gian: " + selectedTime);

        if (originalPostList == null || originalPostList.isEmpty()) {
            Log.d("DEBUG", "Danh sách bài viết gốc rỗng hoặc chưa load xong.");
            return;
        }

        // Log tiêu đề từng bài trước khi lọc
        for (com.example.findlostitemsapp.model.Post post : originalPostList) {
            Log.d("DEBUG", "Tiêu đề: " + post.getTitle() + " | Mô tả: " + post.getDescription());
        }

        PostFilterUtils filterUtils = new PostFilterUtils();
        List<com.example.findlostitemsapp.model.Post> filteredList = filterUtils.filterPosts(
                originalPostList,  // <-- danh sách gốc đã được load từ Firebase
                keyword,
                selectedTag,
                selectedCategory,
                selectedLocation,
                selectedTime
        );

        Log.d("DEBUG", "Số lượng kết quả sau lọc: " + filteredList.size());
        tvKetQuaSoLuong.setText(String.valueOf(filteredList.size()));

        postAdapter.updatePosts(filteredList);
    }

    private void loadPostsFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<com.example.findlostitemsapp.model.Post> tempList = new ArrayList<>();
                if (!snapshot.exists()) {
                    Log.w("FirebaseData", "Node 'posts' không tồn tại hoặc rỗng");
                    Toast.makeText(SearchActivity.this, "Không có bài đăng nào", Toast.LENGTH_SHORT).show();
                    postList.clear();
                    postAdapter.updatePosts(new ArrayList<>(postList));
                    return;
                }
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        com.example.findlostitemsapp.model.Post post = postSnapshot.getValue(com.example.findlostitemsapp.model.Post.class);
                        if (post != null) {
                            post.setPostId(postSnapshot.getKey());
                            tempList.add(post);
                            Log.d("FirebaseData", "Thêm bài đăng: " + post.getTitle() + ", ID: " + post.getPostId());
                        } else {
                            Log.w("FirebaseData", "Post null từ snapshot: " + postSnapshot.getKey());
                        }
                    } catch (Exception e) {
                        Log.e("FirebaseData", "Lỗi parse post từ snapshot: " + postSnapshot.getKey(), e);
                    }
                }
                // Cập nhật trực tiếp trên main thread
                originalPostList.clear();
                originalPostList.addAll(tempList);
                postList.clear();
                postList.addAll(tempList);
                Log.d("FirebaseData", "Tổng số bài đăng: " + postList.size());
                Log.d("FirebaseData", "Nội dung postList trước updatePosts: " + postList.toString());
                postAdapter.updatePosts(tempList);
                Log.d("FirebaseData", "Tổng số bài đăng sau cập nhật: " + postList.size());
                for (com.example.findlostitemsapp.model.Post p : tempList) {
                    Log.d("FirebaseData", "Bài đăng trong postList: " + p.getTitle());
                }
                if (tempList.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Không tìm thấy bài đăng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Lỗi tải dữ liệu: " + error.getMessage());
                Toast.makeText(SearchActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSpinnerData(String nodeName, List<String> list, CustomSpinnerAdapter adapter,String hintText) {
        databaseReference.child(nodeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                list.add(hintText);
                for (DataSnapshot child : snapshot.getChildren())
                {
                    String value = child.getValue(String.class);
                    Log.d("SpinnerData", "Loaded value: " + value);
                    list.add(value);
                }
                adapter.notifyDataSetChanged();

                Log.d("FirebaseCheck", "Đã set adapter cho spinner: " + nodeName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomNavigationBarAction() {
        bottomNav.setSelectedItemId(R.id.nav_search);
        Map<Integer, Runnable> menuActions = new HashMap<>();
        menuActions.put(R.id.nav_home, () -> openHome());
        menuActions.put(R.id.nav_search, () -> openSearch());
        menuActions.put(R.id.nav_post, () -> openPost());
        menuActions.put(R.id.nav_notifications, () -> openNotification());
        menuActions.put(R.id.nav_profile, () -> openProfile());
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Runnable action = menuActions.get(item.getItemId());
                if (action != null) {
                    action.run();
                    return true;
                }
                return false;

            }
        });
    }

    private void openProfile() {
        Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void openNotification() {
        Intent intent = new Intent(SearchActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    private void openPost() {
        Intent intent = new Intent(SearchActivity.this, PostsActivity.class);
        startActivity(intent);
    }

    private void openHome() {
        Intent intent = new Intent(SearchActivity.this, Home.class);
        startActivity(intent);
    }

    private boolean openSearch() {
        return true;
    }

    private void initUi() {

        bottomNav = findViewById(R.id.bottomNav);
        spinnerLoaiBaiViet = findViewById(R.id.spinnerLoaiBaiViet);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        spinnerThoiGian = findViewById(R.id.spinnerThoiGian);
        spinnerTinhThanh = findViewById(R.id.spinnerTinhThanh);
        btnTimKiem = findViewById(R.id.btnTimKiem);
        edtKeyword = findViewById(R.id.edtKeyword);
        btnTatCa = findViewById(R.id.btnTatCa);
        tvKetQuaSoLuong = findViewById(R.id.tvKetQuaSoLuong);

        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);

        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onPostClick(com.example.findlostitemsapp.model.Post post) {
        gotoPostDetailActivity(post);
    }

    private void gotoPostDetailActivity(com.example.findlostitemsapp.model.Post post) {
        Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
        intent.putExtra("post_data", post);
        startActivity(intent);
    }
}
