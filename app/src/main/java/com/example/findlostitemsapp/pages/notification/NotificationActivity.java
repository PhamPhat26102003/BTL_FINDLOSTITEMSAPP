package com.example.findlostitemsapp.pages.notification;

import static com.example.findlostitemsapp.pages.uiutils.UiUtils.setupBottomNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.post.PostsActivity;
import com.example.findlostitemsapp.pages.profile.ProfileActivity;
import com.example.findlostitemsapp.pages.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initUi();

        setupBottomNavigation(this, bottomNav, R.id.nav_notifications);
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