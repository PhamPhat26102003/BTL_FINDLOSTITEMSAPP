package com.example.findlostitemsapp.pages.login;

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
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.register.Register;
import com.example.findlostitemsapp.pages.uiutils.UiUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSession";
    private static final String LOGIN_METHOD_EMAIL = "email";
    private static final String LOGIN_METHOD_GOOGLE = "google";
    private static final int RC_SIGN_IN = 1001;
    private static final String GOOGLE_CLIENT_ID = "144477079876-jg338ghvhgalea8snoppfk3umvjk8ldi.apps.googleusercontent.com";

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnGoogleLogin;
    private TextView textBreadcrumb, textLoggedOut, textRegister, textHome;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        initializeUi();
        setupGoogleSignIn();
        setupListeners();
        checkLogOutStatus();
    }

    private void initializeUi() {
        progressBar = findViewById(R.id.progressBar);
        textBreadcrumb = findViewById(R.id.textBreadcrumb);
        textRegister = findViewById(R.id.textRegister);
        textLoggedOut = findViewById(R.id.textLoggedOut);
        textHome = findViewById(R.id.textHome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        UiUtils.setColoredSpan(textBreadcrumb, "🏠 Trang chủ > Đăng nhập", "Trang chủ", "#00D46F");
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupListeners() {
        textRegister.setOnClickListener(v -> navigateToActivity(Register.class));
        textHome.setOnClickListener(v -> navigateToActivity(Home.class));
        btnLogin.setOnClickListener(v -> handleEmailLogin());
        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    private void checkLogOutStatus() {
        boolean loggedOut = getIntent().getBooleanExtra("LOGGED_OUT", false);
        textLoggedOut.setVisibility(loggedOut ? View.VISIBLE : View.GONE);
    }

    private void handleEmailLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            fetchUserData(user.getUid());
                        } else {
                            showError("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        showError("Đăng nhập thất bại: " + task.getException().getMessage());
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
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
        return true;
    }

    private void fetchUserData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    // Tạo userName từ firstName và lastName
                    String userName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                    userName = userName.trim();
                    if (userName.isEmpty()) {
                        userName = "Người dùng ẩn danh";
                    }

                    saveUserSession(userName, firstName, lastName, email, phone, address, LOGIN_METHOD_EMAIL);
                    navigateToActivity(Home.class);
                } else {
                    showError("Không tìm thấy thông tin người dùng!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("Lỗi khi truy cập dữ liệu: " + error.getMessage());
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                com.google.android.gms.auth.api.signin.GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(), account.getDisplayName(), account.getEmail());
            } catch (ApiException e) {
                showError("Đăng nhập Google thất bại: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, String displayName, String email) {
        progressBar.setVisibility(View.VISIBLE);
        btnGoogleLogin.setEnabled(false);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Lưu thông tin người dùng vào Firebase Database
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            String userName = displayName != null && !displayName.isEmpty() ? displayName : "Người dùng ẩn danh";
                            userRef.child("userName").setValue(userName);
                            userRef.child("email").setValue(email);

                            saveGoogleUserSession(userName, email);
                            navigateToActivity(Home.class);
                        } else {
                            showError("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        showError("Xác thực Google thất bại: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserSession(String userName, String firstName, String lastName, String email, String phone, String address, String loginMethod) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.putString("loginMethod", loginMethod);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void saveGoogleUserSession(String userName, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("email", email);
        editor.putString("loginMethod", LOGIN_METHOD_GOOGLE);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnGoogleLogin.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}