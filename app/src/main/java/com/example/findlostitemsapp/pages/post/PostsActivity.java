package com.example.findlostitemsapp.pages.post;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.model.Post;
import com.example.findlostitemsapp.pages.home.Home;
import com.example.findlostitemsapp.pages.notification.NotificationActivity;
import com.example.findlostitemsapp.pages.profile.ProfileActivity;
import com.example.findlostitemsapp.pages.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostsActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_LOCATION_PERMISSION = 1002;
    private BottomNavigationView bottomNav;
    private EditText editTitle, editDescription;
    private Spinner spinnerTag, spinnerCategory, spinnerLocation;
    private Button btnPost;
    private DatabaseReference postsRef;


    private ImageView imagePreview;
    private Button btnSelectImage, btnGetCurrentLocation;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        postsRef = firebaseDatabase.getReference("posts");

        initUi();

        loadSpinnerData("tag", spinnerTag);
        loadSpinnerData("itemCategory", spinnerCategory);
        loadSpinnerData("location", spinnerLocation);

        bottomNavigationBarAction();

        btnPost.setOnClickListener(v -> createPost());
    }

    //Hàm lấy dữ liệu tag, category, locaton vào các spinner select
    private void loadSpinnerData(String nodeName, Spinner spinner) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(nodeName);
        ArrayList<String> list = new ArrayList<>();

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String value = snapshot.getValue(String.class);
                    list.add(value);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        PostsActivity.this,
                        android.R.layout.simple_spinner_item,
                        list
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Lỗi tải dữ liệu " + nodeName, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void bottomNavigationBarAction() {
        bottomNav.setSelectedItemId(R.id.nav_post);
        Map<Integer, Runnable> menuActions = new HashMap<>();
        menuActions.put(R.id.nav_home, () -> openHome());
        menuActions.put(R.id.nav_search, () -> openSearch());
        menuActions.put(R.id.nav_post, () -> openPost());
        menuActions.put(R.id.nav_notifications, () -> openNotification());
        menuActions.put(R.id.nav_profile, () -> openProfile());
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Runnable action = menuActions.get(item.getItemId());
                if (action != null) {
                    action.run();
                    return true;
                }
                return false;

            }
        });
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImageToImgBB(String postId, Uri imageUri, Post post) {
        String apiKey = "c9828ca1066bac810b1789ed4a23b7ee";  // Thay bằng API Key của bạn
        String base64Image = encodeImageToBase64(imageUri);

        if (base64Image == null) {
            Toast.makeText(this, "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("key", apiKey)
                .add("image", base64Image)
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(PostsActivity.this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        String imageUrl = json.getJSONObject("data").getString("url");

                        ArrayList<String> imageUrls = new ArrayList<>();
                        imageUrls.add(imageUrl);
                        post.setImageUrls(imageUrls);

                        postsRef.child(postId).setValue(post)
                                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                                    Toast.makeText(PostsActivity.this, "Đăng tin thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                }))
                                .addOnFailureListener(e -> runOnUiThread(() ->
                                        Toast.makeText(PostsActivity.this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                ));
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(PostsActivity.this, "Lỗi xử lý JSON", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(PostsActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    //post
    private void createPost() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String tag = spinnerTag.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String locationText = spinnerLocation.getSelectedItem().toString();

        // Kiểm tra dữ liệu
        if (title.isEmpty() || description.isEmpty() || tag.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một Post mới
        String postId = postsRef.push().getKey();  // Tạo postId tự động
        String postDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Post newPost = new Post();
        newPost.setPostId(postId);
        newPost.setTitle(title);
        newPost.setDescription(description);
        newPost.setTag(tag);
        newPost.setItemCategory(category);
        newPost.setPostDate(postDate);
        newPost.setIsFound(false);  // Mặc định là chưa tìm thấy
        newPost.setViewCount(0);
        newPost.setLocation(locationText);
        newPost.setLostDate("Chưa xác định");
        newPost.setImageUrls(new ArrayList<>());
        newPost.setContactInfo("Chưa có thông tin");

        if (selectedImageUri != null) {
            uploadImageToImgBB(postId, selectedImageUri, newPost);
        } else {
            postsRef.child(postId).setValue(newPost)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(PostsActivity.this, "Đăng tin thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PostsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void openProfile() {
        Intent intent = new Intent(PostsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void openNotification() {
        Intent intent = new Intent(PostsActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    private boolean openPost() {
        return true;
    }

    private void openHome() {
        Intent intent = new Intent(PostsActivity.this, Home.class);
        startActivity(intent);
    }

    private void openSearch() {
        Intent intent = new Intent(PostsActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
        }
    }

    private void initUi() {
        bottomNav = findViewById(R.id.bottomNav);
        // Gán các thành phần UI
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        spinnerTag = findViewById(R.id.spinner_tag);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnPost = findViewById(R.id.btn_post);

        imagePreview = findViewById(R.id.image_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        spinnerLocation = findViewById(R.id.spinner_localtion);
        btnSelectImage.setOnClickListener(v -> openImagePicker());
    }
}






