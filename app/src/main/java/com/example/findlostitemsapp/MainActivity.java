package com.example.findlostitemsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.findlostitemsapp.dbconnect.FirebaseDatabaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseConnection";
    private FirebaseDatabaseHelper firebaseConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo kết nối Firebase
        firebaseConnector = new FirebaseDatabaseHelper();

        // Tạo bảng users và posts
        firebaseConnector.createUsersTable();
        firebaseConnector.createPostsTable();

        // Kiểm tra kết nối
        DatabaseReference usersRef = firebaseConnector.getUsersReference();
        DatabaseReference postsRef = firebaseConnector.getPostsReference();

        Log.d(TAG, "Đã kết nối tới bảng users: " + usersRef.toString());
        Log.d(TAG, "Đã kết nối tới bảng posts: " + postsRef.toString());
    }
}