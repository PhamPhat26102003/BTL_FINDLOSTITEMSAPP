package com.example.findlostitemsapp.pages.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.notification.NotificationActivity;
import com.example.findlostitemsapp.pages.post.PostsActivity;
import com.example.findlostitemsapp.pages.search.SearchActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSession";
    private static final String LOGIN_METHOD_GOOGLE = "google";

    private FirebaseAuth auth;
    private TextView textName, textEmail;
    private ImageView imageViewProfile;
    private Button btnChangePassword, btnUpdateInfo;
    private BottomNavigationView bottomNav;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        initializeUi();
        setupListeners();
        loadUserInfo();
        setupBottomNavigation();
    }

    private void initializeUi() {
        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        btnChangePassword = findViewById(R.id.BtnChangePass);
        btnUpdateInfo = findViewById(R.id.btnUpdateInfo);
        bottomNav = findViewById(R.id.bottomNav);
    }

    private void setupListeners() {
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnUpdateInfo.setOnClickListener(v -> showUpdateInfoDialog());
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu");

        LinearLayout layout = createDialogLayout();
        EditText currentPasswordInput = createPasswordEditText("Mật khẩu hiện tại");
        EditText newPasswordInput = createPasswordEditText("Mật khẩu mới");
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        layout.addView(currentPasswordInput);
        layout.addView(newPasswordInput);
        layout.addView(progressBar);
        builder.setView(layout);

        builder.setPositiveButton("Đổi", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (!validatePasswordInputs(currentPassword, newPassword, currentPasswordInput, newPasswordInput)) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            updatePassword(currentPassword, newPassword, new UpdatePasswordCallback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                    showToast("Đổi mật khẩu thành công!");
                }

                @Override
                public void onFailure(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    showToast("Lỗi: " + errorMessage, Toast.LENGTH_LONG);
                }
            });
        });
    }

    private boolean validatePasswordInputs(String currentPassword, String newPassword, EditText currentInput, EditText newInput) {
        if (currentPassword.isEmpty()) {
            currentInput.setError("Nhập mật khẩu hiện tại");
            return false;
        }
        if (newPassword.isEmpty()) {
            newInput.setError("Nhập mật khẩu mới");
            return false;
        }
        if (newPassword.length() < 6) {
            newInput.setError("Mật khẩu mới ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    private void updatePassword(String currentPassword, String newPassword, UpdatePasswordCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Không tìm thấy người dùng!");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(updateTask.getException().getMessage());
                    }
                });
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }

    private interface UpdatePasswordCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    private void showUpdateInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật thông tin");

        LinearLayout layout = createDialogLayout();
        EditText firstNameInput = createEditText("First Name", sharedPreferences.getString("firstName", ""));
        EditText lastNameInput = createEditText("Last Name", sharedPreferences.getString("lastName", ""));
        EditText phoneInput = createEditText("Số điện thoại", sharedPreferences.getString("phone", ""), InputType.TYPE_CLASS_PHONE);
        EditText addressInput = createEditText("Địa chỉ", sharedPreferences.getString("address", ""));
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        layout.addView(firstNameInput);
        layout.addView(lastNameInput);
        layout.addView(phoneInput);
        layout.addView(addressInput);
        layout.addView(progressBar);
        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                showToast("Vui lòng nhập họ và tên.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            updateUserInfo(firstName, lastName, phone, address, new UpdateInfoCallback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                    showToast("Cập nhật thông tin thành công!");
                    loadUserInfo();
                }

                @Override
                public void onFailure(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    showToast("Cập nhật thất bại: " + errorMessage, Toast.LENGTH_LONG);
                }
            });
        });
    }

    private void updateUserInfo(String firstName, String lastName, String phone, String address, UpdateInfoCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Người dùng không tồn tại.");
            return;
        }

        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("phoneNumber", phone);
        updates.put("address", address);

        userRef.updateChildren(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private interface UpdateInfoCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    private LinearLayout createDialogLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        return layout;
    }

    private EditText createEditText(String hint, String defaultValue, int inputType) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setText(defaultValue);
        editText.setInputType(inputType);
        return editText;
    }

    private EditText createEditText(String hint, String defaultValue) {
        return createEditText(hint, defaultValue, InputType.TYPE_CLASS_TEXT);
    }

    private EditText createPasswordEditText(String hint) {
        return createEditText(hint, "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void loadUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            showToast("Chưa đăng nhập, vui lòng đăng nhập lại");
            return;
        }

        String loginMethod = sharedPreferences.getString("loginMethod", LOGIN_METHOD_GOOGLE);
        if (LOGIN_METHOD_GOOGLE.equals(loginMethod)) {
            loadGoogleUserInfo();
        } else {
            loadFirebaseUserInfo(user.getUid());
        }
    }

    private void loadGoogleUserInfo() {
        GoogleSignInAccount profile = GoogleSignIn.getLastSignedInAccount(this);
        if (profile == null) {
            showToast("Không tìm thấy thông tin Google!");
            return;
        }

        textName.setText(profile.getDisplayName() != null ? profile.getDisplayName() : "Tên người dùng");
        textEmail.setText(profile.getEmail() != null ? profile.getEmail() : "Email người dùng");
        btnUpdateInfo.setEnabled(false);
        btnChangePassword.setEnabled(false);

        String photoUrl = profile.getPhotoUrl() != null ? profile.getPhotoUrl().toString() : null;
        loadProfileImage(photoUrl);
    }

    private void loadFirebaseUserInfo(String userId) {
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
                    String image = snapshot.child("profileImageUrl").getValue(String.class);

                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                    textName.setText(fullName);
                    textEmail.setText(email != null ? email : "");
                    loadProfileImage(image);

                    saveUserInfo(firstName, lastName, email, phone, address, image);
                } else {
                    showToast("Không tìm thấy thông tin người dùng!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Lỗi khi truy xuất dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void loadProfileImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.user_profile)
                .error(R.drawable.user_profile)
                .into(imageViewProfile);
    }

    private void saveUserInfo(String firstName, String lastName, String email, String phone, String address, String profileImageUrl) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.putString("profileImageUrl", profileImageUrl);
        editor.apply();
    }

    private void showToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }

    private void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, Home.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (itemId == R.id.nav_post) {
                startActivity(new Intent(this, PostsActivity.class));
                return true;
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}