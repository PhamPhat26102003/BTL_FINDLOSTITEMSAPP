package com.example.findlostitemsapp.pages.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.register.Register;
import com.example.findlostitemsapp.pages.uiutils.UiUtils;

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