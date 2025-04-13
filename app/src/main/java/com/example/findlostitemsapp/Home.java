package com.example.findlostitemsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    FloatingActionButton fabToggle;
    LinearLayout socialButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // code tạm cho hiện giao diện, sau xử lí sự kiện thì chi hàm ra
        fabToggle = findViewById(R.id.fabToggle);
        socialButtons = findViewById(R.id.socialButtons);
        ImageView btnDropdown = findViewById(R.id.btnDropdown);

        fabToggle.setOnClickListener(v -> {
            if (socialButtons.getVisibility() == View.VISIBLE) {
                socialButtons.setVisibility(View.GONE);
            } else {
                socialButtons.setVisibility(View.VISIBLE);
            }
        });


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

    private void logout() {
        Intent intent = new Intent(Home.this, Login.class);
        intent.putExtra("LOGGED_OUT", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}