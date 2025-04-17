package com.example.findlostitemsapp.pages.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findlostitemsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.uiutils.UiUtils;
import com.example.findlostitemsapp.pages.login.Login;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    TextView textBreadcrumb, textLogin;
    EditText editTextEmail, editTextPassword, editTextRePassword, editTextPhone, editTextFirstName, editTextLastName, editTextAddress;
    Button btnRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.progressBar);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextFirstName = findViewById(R.id.editFirstName);
        editTextLastName = findViewById(R.id.editLastName);
        editTextAddress = findViewById(R.id.editAddress);
        btnRegister = findViewById(R.id.btnRegister);
        textBreadcrumb = findViewById(R.id.textBreadcrumb);
        textLogin = findViewById(R.id.textLogin);
        UiUtils.setColoredSpan(textBreadcrumb, "🏠 Trang chủ > Đăng nhập", "Trang chủ", "#00D46F");

        mAuth = FirebaseAuth.getInstance();
        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String firstname = String.valueOf(editTextFirstName.getText());
                String lastname = String.valueOf(editTextLastName.getText());
                String address = String.valueOf(editTextAddress.getText());
                String phone = String.valueOf(editTextPhone.getText());
                String repassword = String.valueOf(editTextRePassword.getText());
                Integer postsCount = 0;
                String profileImg = "";

                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(()->{
                    if (password.equals(repassword)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Đăng ký thành công
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            String userId = user.getUid();  // Lấy ID người dùng

                                            // Tạo đối tượng User để lưu vào database
                                            User newUser = new User(userId,postsCount,address,phone,profileImg,email,lastname,firstname);

                                            // Lưu đối tượng User vào Firebase Realtime Database
                                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                            usersRef.child(userId).setValue(newUser)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(Register.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.GONE);
                                                            } else {
                                                                Toast.makeText(Register.this, "Lưu thông tin thất bại", Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(Register.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(Register.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                },2000);

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
