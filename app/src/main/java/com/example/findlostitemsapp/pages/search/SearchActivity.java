package com.example.findlostitemsapp.pages.search;

import static com.example.findlostitemsapp.pages.uiutils.UiUtils.setupBottomNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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
import com.example.findlostitemsapp.model.Post;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.home.PostAdapter;
import com.example.findlostitemsapp.pages.post.PostDetailActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements PostAdapter.OnPostClickListener {

    private BottomNavigationView bottomNav;
    private EditText editTextSearch;
    private Button btnTimKiem, btnTatCa;
    private Spinner spinnerLoaiBaiViet, spinnerDanhMuc, spinnerThoiGian, spinnerTinhThanh;
    private TextView tvKetQua;
    private RecyclerView recyclerViewPosts;

    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>(); // Dữ liệu bài đăng đã tải từ Firebase
    private DatabaseReference databaseReference, databaseSpinnerReference;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUi(); // Khởi tạo giao diện
        setupBottomNavigation(this, bottomNav, R.id.nav_search); // Thiết lập thanh điều hướng dưới
        setupButtonActions(); // Cài đặt hành động cho nút
        setupRecyclerView(); // Cài đặt RecyclerView

        databaseReference = FirebaseDatabase.getInstance().getReference("posts"); // Tham chiếu đến Firebase
        databaseSpinnerReference = FirebaseDatabase.getInstance().getReference();

        // Load dữ liệu cho các spinner
        loadSpinnerData("tag", spinnerLoaiBaiViet, "Loại bài viết");
        loadSpinnerData("itemCategory", spinnerDanhMuc, "Danh mục");
        loadSpinnerData("time_filters", spinnerThoiGian, "Thời gian");
        loadSpinnerData("location", spinnerTinhThanh, "Tỉnh/Thành phố");

        // Load bài đăng từ Firebase khi mở ứng dụng
        loadPostsFromFirebase();
    }

    private void initUi() {
        bottomNav = findViewById(R.id.bottomNav);
        editTextSearch = findViewById(R.id.editTextSearch);
        btnTimKiem = findViewById(R.id.btnTimKiem);
        btnTatCa = findViewById(R.id.btnTatCa);
        spinnerLoaiBaiViet = findViewById(R.id.spinnerLoaiBaiViet);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        spinnerThoiGian = findViewById(R.id.spinnerThoiGian);
        spinnerTinhThanh = findViewById(R.id.spinnerTinhThanh);
        tvKetQua = findViewById(R.id.tvKetQua);
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(this, postList, this); // Cài đặt Adapter cho RecyclerView
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(postAdapter);
    }

    private void setupButtonActions() {
        btnTimKiem.setOnClickListener(v -> performSearch()); // Khi nhấn tìm kiếm
        btnTatCa.setOnClickListener(v -> showAllPosts()); // Khi nhấn xem tất cả bài đăng
    }

    private void loadSpinnerData(String nodeName, Spinner spinner, String hintText) {
        databaseSpinnerReference.child(nodeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> items = new ArrayList<>();
                items.add(hintText);
                for (DataSnapshot child : snapshot.getChildren()) {
                    String value = child.getValue(String.class);
                    if (value != null) {
                        items.add(value);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this,
                        android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchActivity", "Error loading spinner data: " + error.getMessage());
                Toast.makeText(SearchActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load các bài đăng từ Firebase
    private void loadPostsFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> tempList = new ArrayList<>();
                if (!snapshot.exists()) {
                    Log.w("FirebaseData", "Node 'posts' không tồn tại hoặc rỗng");
                    Toast.makeText(SearchActivity.this, "Không có bài đăng nào", Toast.LENGTH_SHORT).show();
                    postList.clear();
                    postAdapter.updatePosts(new ArrayList<>(postList)); // Cập nhật giao diện
                    return;
                }
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Post post = postSnapshot.getValue(Post.class);
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
                // Cập nhật dữ liệu bài đăng lên giao diện
                postList.clear();
                postList.addAll(tempList);
                postAdapter.updatePosts(tempList);
                Log.d("FirebaseData", "Tổng số bài đăng sau cập nhật: " + postList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Lỗi tải dữ liệu: " + error.getMessage());
                Toast.makeText(SearchActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thực hiện tìm kiếm theo từ khóa và các bộ lọc
    private void performSearch() {
        String keyword = editTextSearch.getText().toString().trim().toLowerCase();
        String loaiBaiViet = spinnerLoaiBaiViet.getSelectedItem() != null &&
                !spinnerLoaiBaiViet.getSelectedItem().toString().equals("Loại bài viết") ?
                spinnerLoaiBaiViet.getSelectedItem().toString() : "";
        String danhMuc = spinnerDanhMuc.getSelectedItem() != null &&
                !spinnerDanhMuc.getSelectedItem().toString().equals("Danh mục") ?
                spinnerDanhMuc.getSelectedItem().toString() : "";
        String tinhThanh = spinnerTinhThanh.getSelectedItem() != null &&
                !spinnerTinhThanh.getSelectedItem().toString().equals("Tỉnh/Thành phố") ?
                spinnerTinhThanh.getSelectedItem().toString() : "";
        String thoiGian = spinnerThoiGian.getSelectedItem() != null &&
                !spinnerThoiGian.getSelectedItem().toString().equals("Thời gian") ?
                spinnerThoiGian.getSelectedItem().toString() : "";

        List<Post> filteredList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        // Lọc bài đăng theo các tiêu chí tìm kiếm
        for (Post post : postList) {
            boolean match = true;

            // Kiểm tra từ khóa tìm kiếm
            if (!keyword.isEmpty()) {
                String title = post.getTitle() != null ? post.getTitle().toLowerCase() : "";
                String description = post.getDescription() != null ? post.getDescription().toLowerCase() : "";
                if (!title.contains(keyword) && !description.contains(keyword)) {
                    match = false;
                }
            }

            // Lọc theo các bộ lọc khác
            if (!loaiBaiViet.isEmpty() && !post.getTag().equalsIgnoreCase(loaiBaiViet)) {
                match = false;
            }
            if (!danhMuc.isEmpty() && !post.getItemCategory().equalsIgnoreCase(danhMuc)) {
                match = false;
            }
            if (!tinhThanh.isEmpty() && !post.getLocation().equalsIgnoreCase(tinhThanh)) {
                match = false;
            }
            if (!thoiGian.isEmpty()) {
                try {
                    Date postDate = dateFormat.parse(post.getPostDate());
                    long postTime = postDate != null ? postDate.getTime() : 0;
                    long daysDiff = (currentTime - postTime) / (1000 * 60 * 60 * 24);

                    switch (thoiGian) {
                        case "1 tuần":
                            if (daysDiff > 7) match = false;
                            break;
                        case "1 tháng":
                            if (daysDiff > 30) match = false;
                            break;
                        case "3 tháng":
                            if (daysDiff > 90) match = false;
                            break;
                        case "6 tháng":
                            if (daysDiff > 180) match = false;
                            break;
                    }
                } catch (ParseException e) {
                    Log.e("SearchActivity", "Error parsing date: " + post.getPostDate(), e);
                    match = false;
                }
            }

            if (match) {
                filteredList.add(post);
            }
        }

        postAdapter.updatePosts(filteredList);
        tvKetQua.setText("Kết quả tìm kiếm: " + filteredList.size());
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy bài đăng phù hợp.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đã tìm thấy " + filteredList.size() + " bài đăng.", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị tất cả bài đăng
    private void showAllPosts() {
        postAdapter.updatePosts(postList); // Cập nhật lại dữ liệu cho RecyclerView
        tvKetQua.setText("Tổng số bài đăng: " + postList.size()); // Hiển thị tổng số bài đăng
        Toast.makeText(this, "Hiển thị tất cả " + postList.size() + " bài đăng.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostClick(Post post) {
        // Chuyển đến màn hình chi tiết bài đăng khi người dùng nhấn vào bài đăng
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("post_data", post);
        startActivity(intent);
    }

}
