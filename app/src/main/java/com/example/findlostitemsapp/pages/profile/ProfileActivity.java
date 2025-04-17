package com.example.findlostitemsapp.pages.profile;

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
import com.example.findlostitemsapp.pages.notification.NotificationActivity;
import com.example.findlostitemsapp.pages.post.Post;
import com.example.findlostitemsapp.pages.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUi();

        bottomNavigationBarAction();
    }

    private void bottomNavigationBarAction() {
        bottomNav.setSelectedItemId(R.id.nav_profile);
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

    private boolean openProfile() {
        return true;
    }

    private void openNotification() {
        Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    private void openPost() {
        Intent intent = new Intent(ProfileActivity.this, Post.class);
        startActivity(intent);
    }

    private void openHome() {
        Intent intent = new Intent(ProfileActivity.this, Home.class);
        startActivity(intent);
    }

    private void openSearch() {
        Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void initUi() {
        bottomNav = findViewById(R.id.bottomNav);
    }
}