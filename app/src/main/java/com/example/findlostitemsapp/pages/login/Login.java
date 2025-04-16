package com.example.findlostitemsapp.pages.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.register.Register;
import com.example.findlostitemsapp.pages.uiutils.UiUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextView textBreadcrumb, textLoggedOut,textRegister;
    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textBreadcrumb = findViewById(R.id.textBreadcrumb);
        textRegister = findViewById(R.id.textRegister);
        textLoggedOut = findViewById(R.id.textLoggedOut);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        UiUtils.setColoredSpan(textBreadcrumb, "🏠 Trang chủ > Đăng nhập", "Trang chủ", "#00D46F");
        mAuth = FirebaseAuth.getInstance();
        checkLogOut();
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editTextEmail.getText()).trim();
                String password = String.valueOf(editTextPassword.getText()).trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userId = user.getUid();

                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                //lay data gi thi lay
                                                String firstName = snapshot.child("firstname").getValue(String.class);
                                                Toast.makeText(Login.this, "Chào mừng " + firstName, Toast.LENGTH_SHORT).show();

                                                 Intent intent = new Intent(Login.this, Home.class);
                                                 startActivity(intent);
                                                 finish();

                                            } else {
                                                Toast.makeText(Login.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(Login.this, "Lỗi khi truy cập dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(Login.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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