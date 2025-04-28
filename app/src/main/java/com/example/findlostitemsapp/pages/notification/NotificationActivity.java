package com.example.findlostitemsapp.pages.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.model.NotificationModel;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.post.*;
import com.example.findlostitemsapp.pages.profile.ProfileActivity;
import com.example.findlostitemsapp.pages.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initUi();

        bottomNavigationBarAction();

        loadNotification();
    }

    private void loadNotification() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Danh sách mẫu
        notificationList = new ArrayList<>();
        notificationList.add(new NotificationModel("Báo mất đồ thành công", "Yêu cầu của bạn đã được ghi nhận", "2 phút trước"));
        notificationList.add(new NotificationModel("Đồ vật đã được tìm thấy", "Đội ngũ của chúng tôi đã tìm được ví của bạn", "1 giờ trước"));
        notificationList.add(new NotificationModel("Xác minh tài khoản", "Vui lòng xác minh email để sử dụng đầy đủ chức năng", "Hôm qua"));

        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);
    }

    private void bottomNavigationBarAction() {
        bottomNav.setSelectedItemId(R.id.nav_notifications);
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
        Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private boolean openNotification() {
        return true;
    }

    private void openPost() {
        Intent intent = new Intent(NotificationActivity.this, PostsActivity.class);
        startActivity(intent);
    }

    private void openHome() {
        Intent intent = new Intent(NotificationActivity.this, Home.class);
        startActivity(intent);
    }

    private void openSearch() {
        Intent intent = new Intent(NotificationActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void initUi() {
        bottomNav = findViewById(R.id.bottomNav);
    }
}