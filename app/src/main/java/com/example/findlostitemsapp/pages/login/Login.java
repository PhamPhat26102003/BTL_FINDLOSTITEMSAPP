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

    // Các hằng số xác định phương thức đăng nhập và ID Google Client
    private static final String PREFS_NAME = "UserSession"; // Tên file SharedPreferences
    private static final String LOGIN_METHOD_EMAIL = "email"; // Phương thức đăng nhập qua email
    private static final String LOGIN_METHOD_GOOGLE = "google"; // Phương thức đăng nhập qua Google
    private static final int RC_SIGN_IN = 1001; // Mã yêu cầu đăng nhập Google
    private static final String GOOGLE_CLIENT_ID = "144477079876-jg338ghvhgalea8snoppfk3umvjk8ldi.apps.googleusercontent.com"; // Client ID cho Google Sign-In

    private FirebaseAuth auth; // Đối tượng FirebaseAuth để quản lý xác thực người dùng
    private GoogleSignInClient googleSignInClient; // Đối tượng để quản lý Google Sign-In
    private EditText editTextEmail, editTextPassword; // Các trường nhập liệu cho email và mật khẩu
    private Button btnLogin, btnGoogleLogin; // Các nút đăng nhập qua email và Google
    private TextView textBreadcrumb, textLoggedOut, textRegister, textHome; // Các phần tử UI khác
    private ProgressBar progressBar; // Thanh tiến trình để thông báo khi đang xử lý
    private SharedPreferences sharedPreferences; // Đối tượng SharedPreferences để lưu trữ thông tin phiên đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Thiết lập giao diện của Activity

        auth = FirebaseAuth.getInstance(); // Khởi tạo đối tượng FirebaseAuth
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Lấy SharedPreferences
        initializeUi(); // Khởi tạo các phần tử UI
        setupGoogleSignIn(); // Thiết lập Google Sign-In
        setupListeners(); // Cài đặt các sự kiện cho các nút
        checkLogOutStatus(); // Kiểm tra trạng thái đăng xuất
    }

    // Phương thức khởi tạo giao diện người dùng
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

        // Tạo đoạn văn bản có màu cho phần breadcrumb
        UiUtils.setColoredSpan(textBreadcrumb, "🏠 Trang chủ > Đăng nhập", "Trang chủ", "#00D46F");
    }

    // Phương thức thiết lập Google Sign-In
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_CLIENT_ID) // Yêu cầu ID Token
                .requestEmail() // Yêu cầu email
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso); // Tạo đối tượng GoogleSignInClient
    }

    // Phương thức thiết lập các sự kiện cho các phần tử UI
    private void setupListeners() {
        // Khi nhấn vào "Đăng ký", chuyển sang trang Đăng ký
        textRegister.setOnClickListener(v -> navigateToActivity(Register.class));
        // Khi nhấn vào "Trang chủ", chuyển sang trang Trang chủ
        textHome.setOnClickListener(v -> navigateToActivity(Home.class));
        // Khi nhấn vào nút Đăng nhập với Email
        btnLogin.setOnClickListener(v -> handleEmailLogin());
        // Khi nhấn vào nút Đăng nhập với Google
        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    // Kiểm tra trạng thái đăng xuất khi vào màn hình đăng nhập
    private void checkLogOutStatus() {
        boolean loggedOut = getIntent().getBooleanExtra("LOGGED_OUT", false);
        textLoggedOut.setVisibility(loggedOut ? View.VISIBLE : View.GONE); // Hiển thị thông báo nếu người dùng đã đăng xuất
    }

    // Phương thức xử lý đăng nhập với Email
    private void handleEmailLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return; // Kiểm tra đầu vào
        }

        progressBar.setVisibility(View.VISIBLE); // Hiển thị thanh tiến trình
        btnLogin.setEnabled(false); // Vô hiệu hóa nút đăng nhập trong khi đang xử lý

        auth.signInWithEmailAndPassword(email, password) // Gọi Firebase để xác thực người dùng
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) { // Nếu đăng nhập thành công
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            fetchUserData(user.getUid()); // Lấy dữ liệu người dùng từ Firebase Database
                        } else {
                            showError("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        showError("Đăng nhập thất bại: " + task.getException().getMessage());
                    }
                });
    }

    // Phương thức kiểm tra tính hợp lệ của đầu vào email và mật khẩu
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

    // Lấy dữ liệu người dùng từ Firebase Database
    private void fetchUserData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy các thông tin người dùng
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    // Tạo tên người dùng từ firstName và lastName
                    String userName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                    userName = userName.trim();
                    if (userName.isEmpty()) {
                        userName = "Người dùng ẩn danh";
                    }

                    saveUserSession(userName, firstName, lastName, email, phone, address, LOGIN_METHOD_EMAIL); // Lưu thông tin người dùng vào SharedPreferences
                    navigateToActivity(Home.class); // Chuyển sang trang chính
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

    // Phương thức đăng nhập với Google
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN); // Khởi tạo intent đăng nhập Google
    }

    // Xử lý kết quả trả về từ Google Sign-In
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

    // Xác thực người dùng thông qua Google
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

                            saveGoogleUserSession(userName, email); // Lưu thông tin đăng nhập Google
                            navigateToActivity(Home.class); // Chuyển sang trang chính
                        } else {
                            showError("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        showError("Xác thực Google thất bại: " + task.getException().getMessage());
                    }
                });
    }

    // Lưu thông tin người dùng sau khi đăng nhập qua Email
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
        editor.apply(); // Lưu thông tin vào SharedPreferences
    }

    // Lưu thông tin người dùng sau khi đăng nhập qua Google
    private void saveGoogleUserSession(String userName, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("email", email);
        editor.putString("loginMethod", LOGIN_METHOD_GOOGLE);
        editor.putBoolean("isLoggedIn", true);
        editor.apply(); // Lưu thông tin vào SharedPreferences
    }

    // Điều hướng đến màn hình mới
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng màn hình hiện tại
    }

    // Hiển thị thông báo lỗi
    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnGoogleLogin.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
