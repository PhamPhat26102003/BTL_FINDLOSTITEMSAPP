package com.example.findlostitemsapp.pages.uiutils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.login.Login;
import com.example.findlostitemsapp.pages.notification.NotificationActivity;
import com.example.findlostitemsapp.pages.post.PostsActivity;
import com.example.findlostitemsapp.pages.profile.ProfileActivity;
import com.example.findlostitemsapp.pages.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UiUtils {
    public static void setColoredSpan(TextView textView, String fullText, String textToColor, String colorHex) {
        SpannableString spannable = new SpannableString(fullText);

        int start = fullText.indexOf(textToColor);
        if (start >= 0) {
            int end = start + textToColor.length();
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor(colorHex)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        textView.setText(spannable);
    }


        public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNav, int selectedItemId) {
            bottomNav.setSelectedItemId(selectedItemId);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                Intent intent = null;
                SharedPreferences sharedPreferences = activity.getSharedPreferences("UserSession", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                if (itemId == R.id.nav_home) {
                    intent = new Intent(activity, Home.class);
                } else if (itemId == R.id.nav_search) {
                    intent = new Intent(activity, SearchActivity.class);
                } else if (itemId == R.id.nav_post) {
                    if(isLoggedIn){
                        intent = new Intent(activity, PostsActivity.class);
                    }else{
                        intent = new Intent(activity, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }

                } else if (itemId == R.id.nav_notifications) {
                    intent = new Intent(activity, NotificationActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(activity, ProfileActivity.class);
                }

                if (intent != null && itemId != selectedItemId) { // Không reload lại chính mình
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0); // Không có hiệu ứng chuyển (nếu muốn)
                    activity.finish(); // Để tránh trồng Activity
                    return true;
                }
                return false;
            });
        }
    }
