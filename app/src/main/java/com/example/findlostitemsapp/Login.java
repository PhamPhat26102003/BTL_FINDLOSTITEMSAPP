package com.example.findlostitemsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    TextView textBreadcrumb, textLoggedOut,textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textBreadcrumb = findViewById(R.id.textBreadcrumb);
        textRegister = findViewById(R.id.textRegister);
        textLoggedOut = findViewById(R.id.textLoggedOut);
        UiUtils.setColoredSpan(textBreadcrumb, "🏠 Trang chủ > Đăng nhập", "Trang chủ", "#00D46F");
        checkLogOut();

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });

    }

    private void goToRegisterActivity() {
        Intent intent = new Intent(Login.this, Register.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void checkLogOut() {
        boolean loggedOut = getIntent().getBooleanExtra("LOGGED_OUT", false);

        if (loggedOut) {
            textLoggedOut.setVisibility(View.VISIBLE);
        } else {
            textLoggedOut.setVisibility(View.GONE);
        }
    }
}