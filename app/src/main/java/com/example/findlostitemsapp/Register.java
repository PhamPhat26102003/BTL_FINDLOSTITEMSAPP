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

public class Register extends AppCompatActivity {

    TextView textBreadcrumb, textLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textBreadcrumb = findViewById(R.id.textBreadcrumb);
        textLogin = findViewById(R.id.textLogin);
        UiUtils.setColoredSpan(textBreadcrumb, "🏠 Trang chủ > Đăng nhập", "Trang chủ", "#00D46F");

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(Register.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}