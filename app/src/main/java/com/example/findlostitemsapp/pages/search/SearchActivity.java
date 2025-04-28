package com.example.findlostitemsapp.pages.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.home.PostAdapter;
import com.example.findlostitemsapp.pages.notification.NotificationActivity;
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

public class SearchActivity extends AppCompatActivity implements PostAdapter.OnPostClickListener{

    private BottomNavigationView bottomNav;
    private Spinner spinnerLoaiBaiViet, spinnerDanhMuc, spinnerThoiGian, spinnerTinhThanh;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<com.example.findlostitemsapp.model.Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUi();

        bottomNavigationBarAction();

        //loda data spinner
        List<String> listLoaiBaiViet = new ArrayList<>();
        List<String> listDanhMuc = new ArrayList<>();
        List<String> listThoiGian = new ArrayList<>();
        List<String> listTinhThanh = new ArrayList<>();
        loadSpinnerData("post_types",listLoaiBaiViet,spinnerLoaiBaiViet,"Loại bài viết");
        loadSpinnerData("categories",listDanhMuc,spinnerDanhMuc,"Danh mục");
        loadSpinnerData("time_filters",listThoiGian,spinnerThoiGian,"Thời gian");
        loadSpinnerData("locations",listTinhThanh,spinnerTinhThanh,"Tỉnh/Thành phố");

        // Khởi tạo RecyclerView và PostAdapter
        postList = new ArrayList<>();
        postAdapter = new PostAdapter( this, postList, (PostAdapter.OnPostClickListener) this);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(postAdapter);
        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        // Lấy dữ liệu từ Firebase
        loadPostsFromFirebase();
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

    private void loadSpinnerData(String nodeName, List<String> list, Spinner spinner,String hintText) {
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                Log.d("FirebaseCheck", "Đã set adapter cho spinner: " + nodeName);
                spinner.setSelection(0);

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
