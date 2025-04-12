package com.example.findlostitemsapp;

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

//            popupMenu.setOnMenuItemClickListener(item -> {
//                switch (item.getItemId()) {
//                    case R.id.menu_profile:
//                        return true;
//                    case R.id.menu_history:
//                        return true;
//                    case R.id.menu_settings:
//                        return true;
//                    case R.id.menu_logout:
//                        return true;
//                    default:
//                        return false;
//                }
//            });

            popupMenu.show();
        });


    }
}