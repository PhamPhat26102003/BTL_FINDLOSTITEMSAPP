package com.example.findlostitemsapp.pages.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.model.Post;
import com.example.findlostitemsapp.pages.login.Login;
import com.example.findlostitemsapp.pages.post.PostDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements PostAdapter.OnPostClickListener {
    FloatingActionButton fabToggle;
    LinearLayout socialButtons;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo các view
        fabToggle = findViewById(R.id.fabToggle);
        socialButtons = findViewById(R.id.socialButtons);
        ImageView btnDropdown = findViewById(R.id.btnDropdown);
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);

        // Khởi tạo RecyclerView và PostAdapter
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList, this);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(postAdapter);

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        // Lấy dữ liệu từ Firebase
        loadPostsFromFirebase();

        // Xử lý sự kiện FAB (hiển thị/ẩn các nút mạng xã hội)
        fabToggle.setOnClickListener(v -> {
            if (socialButtons.getVisibility() == View.VISIBLE) {
                socialButtons.setVisibility(View.GONE);
            } else {
                socialButtons.setVisibility(View.VISIBLE);
            }
        });

        // Xử lý dropdown menu
        btnDropdown.setOnClickListener(view -> {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(Home.this, R.style.PopupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, view);
            popupMenu.getMenuInflater().inflate(R.menu.account_dropdown_menu, popupMenu.getMenu());
            Map<Integer, Runnable> menuActions = new HashMap<>();
            //            menuActions.put(R.id.menu_profile, () -> openProfile());
//            menuActions.put(R.id.menu_history, () -> openHistory());
//            menuActions.put(R.id.menu_settings, () -> openSettings());
            menuActions.put(R.id.menu_logout, () -> logout());
            popupMenu.setOnMenuItemClickListener(item -> {
                Runnable action = menuActions.get(item.getItemId());
                if (action != null) {
                    action.run();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void loadPostsFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> tempList = new ArrayList<>();
                if (!snapshot.exists()) {
                    Log.w("FirebaseData", "Node 'posts' không tồn tại hoặc rỗng");
                    Toast.makeText(Home.this, "Không có bài đăng nào", Toast.LENGTH_SHORT).show();
                    postList.clear();
                    postAdapter.updatePosts(new ArrayList<>(postList));
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
                // Cập nhật trực tiếp trên main thread
                postList.clear();
                postList.addAll(tempList);
                Log.d("FirebaseData", "Tổng số bài đăng: " + postList.size());
                Log.d("FirebaseData", "Nội dung postList trước updatePosts: " + postList.toString());
                postAdapter.updatePosts(tempList);
                Log.d("FirebaseData", "Tổng số bài đăng sau cập nhật: " + postList.size());
                for (Post p : tempList) {
                    Log.d("FirebaseData", "Bài đăng trong postList: " + p.getTitle());
                }
                if (tempList.isEmpty()) {
                    Toast.makeText(Home.this, "Không tìm thấy bài đăng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Lỗi tải dữ liệu: " + error.getMessage());
                Toast.makeText(Home.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void logout() {
        Intent intent = new Intent(Home.this, Login.class);
        intent.putExtra("LOGGED_OUT", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPostClick(Post post) {
        gotoPostDetailActivity(post);
    }

    private void gotoPostDetailActivity(Post post) {
        Intent intent = new Intent(Home.this, PostDetailActivity.class);
        intent.putExtra("post_data",post);
        startActivity(intent);
    }
}