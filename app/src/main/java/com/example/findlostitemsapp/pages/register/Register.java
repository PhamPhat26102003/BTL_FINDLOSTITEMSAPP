package com.example.findlostitemsapp.pages.register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.model.User;
import com.example.findlostitemsapp.pages.login.Login;
import com.example.findlostitemsapp.pages.uiutils.UiUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSession";
    private static final String DEFAULT_PROFILE_IMAGE = "https://cdn-icons-png.flaticon.com/512/4975/4975733.png";
    private static final int MIN_PASSWORD_LENGTH = 6;

    private FirebaseAuth auth;
    private EditText editTextEmail, editTextPassword, editTextRePassword, editTextPhone;
    private EditText editTextFirstName, editTextLastName, editTextAddress;
    private Button btnRegister;
    private TextView textBreadcrumb, textLogin;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        initializeUi();
        setupListeners();
    }

    private void initializeUi() {
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

        UiUtils.setColoredSpan(textBreadcrumb, "🏠 Trang chủ > Đăng ký", "Trang chủ", "#00D46F");
    }

    private void setupListeners() {
        textLogin.setOnClickListener(v -> navigateToLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String rePassword = editTextRePassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (!validateInputs(email, password, rePassword, firstName, lastName)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        registerUser(email, password, firstName, lastName, phone, address);
    }

    private boolean validateInputs(String email, String password, String rePassword, String firstName, String lastName) {
        if (email.isEmpty()) {
            editTextEmail.setError("Vui lòng nhập email");
            editTextEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Vui lòng nhập mật khẩu");
            editTextPassword.requestFocus();
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            editTextPassword.setError("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
            editTextPassword.requestFocus();
            return false;
        }
        if (!password.equals(rePassword)) {
            editTextRePassword.setError("Mật khẩu không khớp");
            editTextRePassword.requestFocus();
            return false;
        }
        if (firstName.isEmpty()) {
            editTextFirstName.setError("Vui lòng nhập tên");
            editTextFirstName.requestFocus();
            return false;
        }
        if (lastName.isEmpty()) {
            editTextLastName.setError("Vui lòng nhập họ");
            editTextLastName.requestFocus();
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password, String firstName, String lastName, String phone, String address) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user.getUid(), email, firstName, lastName, phone, address);
                        } else {
                            showError("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        showError("Đăng ký thất bại: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserToDatabase(String userId, String email, String firstName, String lastName, String phone, String address) {
        // Tạo userName từ firstName và lastName
        String userName = firstName + " " + lastName;

        // Tạo đối tượng User
        User newUser = new User(userId, 0, address, phone, DEFAULT_PROFILE_IMAGE, email, lastName, firstName);

        // Lưu thông tin vào Firebase Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.child("userName").setValue(userName);
        usersRef.child("email").setValue(email);
        usersRef.child("firstName").setValue(firstName);
        usersRef.child("lastName").setValue(lastName);
        usersRef.child("phoneNumber").setValue(phone);
        usersRef.child("address").setValue(address);
        usersRef.child("profileImage").setValue(DEFAULT_PROFILE_IMAGE);
        usersRef.child("role").setValue(0);

        usersRef.setValue(newUser)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Lưu userName vào SharedPreferences
                        saveUserSession(userName, firstName, lastName, email, phone, address);
                        showToast("Đăng ký thành công!");
                        navigateToLogin();
                    } else {
                        showError("Lưu thông tin thất bại: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserSession(String userName, String firstName, String lastName, String email, String phone, String address) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.putString("loginMethod", "email");
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}