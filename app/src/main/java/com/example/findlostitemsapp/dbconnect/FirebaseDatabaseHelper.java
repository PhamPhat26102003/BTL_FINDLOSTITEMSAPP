package com.example.findlostitemsapp.dbconnect;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseHelper {
    private static FirebaseDatabaseHelper instance;
    private DatabaseReference databaseReference;

    public FirebaseDatabaseHelper() {
        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized FirebaseDatabaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseHelper();
        }
        return instance;
    }

    // Tạo bảng users nếu chưa tồn tại
    public void createUsersTable() {
        databaseReference.child("users").keepSynced(true);
    }

    // Tạo bảng posts nếu chưa tồn tại
    public void createPostsTable() {
        databaseReference.child("posts").keepSynced(true);
    }

    public DatabaseReference getUsersReference() {
        return databaseReference.child("users");
    }

    public DatabaseReference getPostsReference() {
        return databaseReference.child("posts");
    }
}
