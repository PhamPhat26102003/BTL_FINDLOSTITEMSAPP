package com.example.findlostitemsapp.pages.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.model.Post;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // Hằng số định danh phương thức đăng nhập
    private static final String LOGIN_METHOD_GOOGLE = "google";
    private static final String PREFS_NAME = "UserSession";
    private static final String[] DATE_FORMATS = {"yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy"};

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final List<Post> postList = new ArrayList<>();
    private final OnPostClickListener onPostClickListener;

    // Interface dùng để xử lý sự kiện khi click vào 1 bài viết
    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    // Constructor nhận context, danh sách bài viết và listener click
    public PostAdapter(Context context, List<Post> posts, OnPostClickListener listener) {
        this.context = Objects.requireNonNull(context, "Context cannot be null");
        this.onPostClickListener = listener;
        if (posts != null) this.postList.addAll(posts);
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setHasStableIds(true); // Cố định ID cho mỗi item giúp RecyclerView tối ưu
    }

    // Tạo ViewHolder cho từng item
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    // Gán dữ liệu cho ViewHolder tại vị trí position
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        if (post == null) return;

        // Gán thông tin cơ bản như tiêu đề, mô tả, hình ảnh
        bindBasicInfo(holder, post);

        // Hiển thị ngày đăng bài theo dạng "Hôm nay", "1 ngày trước", v.v.
        bindPostDate(holder, post.getPostDate());

        // Hiển thị thông tin người đăng (tên, vai trò)
        bindUserInfo(holder, post.getUserId());

        // Nếu người dùng là admin thì hiển thị nút xóa
        bindDeleteOption(holder, post, position);

        // Xử lý sự kiện click vào toàn bộ bài viết
        holder.itemView.setOnClickListener(v -> {
            if (onPostClickListener != null) onPostClickListener.onPostClick(post);
        });
    }

    // Gán các thông tin cơ bản của bài viết
    private void bindBasicInfo(PostViewHolder holder, Post post) {
        holder.tvTitle.setText(post.getTitle());
        holder.tvShortDesc.setText(post.getDescription());

        // Nếu có ảnh thì load ảnh bằng Glide, nếu không thì dùng ảnh mặc định
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(post.getImageUrls().get(0))
                    .placeholder(R.drawable.minhhoa)
                    .error(R.drawable.minhhoa)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivImagePost);
        } else {
            holder.ivImagePost.setImageResource(R.drawable.minhhoa);
        }

        // Nếu tag là "Tình trạng cấp" thì hiển thị nhãn "Tin nổi bật"
        holder.tvTagHighlight.setVisibility("Tình trạng cấp".equals(post.getTag()) ? View.VISIBLE : View.GONE);
        holder.tvTagHighlight.setText("Tin nổi bật");

        // Hiển thị số lượt xem, số điện thoại, địa điểm
        holder.tvViews.setText("👁 " + post.getViewCount() + " lượt xem");
        holder.tvContact.setText("📞 " + post.getContactInfo());
        holder.tvLocation.setText("📍 " + post.getLocation());
    }

    // Gán ngày đăng bài và thời gian tương đối
    private void bindPostDate(PostViewHolder holder, String dateStr) {
        long postTime = parseDateToMillis(dateStr); // Chuyển đổi ngày thành mili giây
        long diffDays = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - postTime);

        // Hiển thị "Hôm nay", "1 ngày trước" hoặc "x ngày trước"
        if (diffDays == 0) {
            holder.tvTimeAgo.setText("🕒 Hôm nay");
        } else if (diffDays == 1) {
            holder.tvTimeAgo.setText("🕒 1 ngày trước");
        } else {
            holder.tvTimeAgo.setText("🕒 " + diffDays + " ngày trước");
        }

        // Hiển thị ngày định dạng dd-MM-yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.tvPostDate.setText("📅 " + sdf.format(new Date(postTime)));
    }

    // Hàm chuyển chuỗi ngày thành mili giây (timestamp)
    private long parseDateToMillis(String dateStr) {
        for (String format : DATE_FORMATS) {
            try {
                Date date = new SimpleDateFormat(format, Locale.getDefault()).parse(dateStr);
                if (date != null) return date.getTime();
            } catch (ParseException ignored) {}
        }
        return System.currentTimeMillis(); // Nếu lỗi thì trả về thời điểm hiện tại
    }

    // Lấy thông tin người dùng (tên) từ Firebase hoặc Google
    private void bindUserInfo(PostViewHolder holder, String userId) {
        if (userId == null) {
            holder.tvRole.setText("👤 Người dùng ẩn danh");
            return;
        }

        String loginMethod = sharedPreferences.getString("loginMethod", LOGIN_METHOD_GOOGLE);
        if (LOGIN_METHOD_GOOGLE.equals(loginMethod)) {
            // Nếu đăng nhập bằng Google
            GoogleSignInAccount profile = GoogleSignIn.getLastSignedInAccount(context);
            holder.tvRole.setText("👤 " + (profile != null ? profile.getDisplayName() : "Người dùng"));
        } else {
            // Nếu đăng nhập bằng email/phone, lấy thông tin từ Firebase Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String first = snapshot.child("firstName").getValue(String.class);
                    String last = snapshot.child("lastName").getValue(String.class);
                    holder.tvRole.setText("👤 " + (first != null ? first : "") + " " + (last != null ? last : ""));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    holder.tvRole.setText("👤 Không xác định");
                }
            });
        }
    }

    // Nếu là admin thì cho phép xóa bài viết
    private void bindDeleteOption(PostViewHolder holder, Post post, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && "admin@gmail.com".equals(user.getEmail())) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> confirmDelete(post.getPostId(), position));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    // Hiển thị hộp thoại xác nhận xóa
    private void confirmDelete(String postId, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài viết này?")
                .setPositiveButton("Xóa", (dialog, which) -> deletePost(postId, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Xóa bài viết khỏi Firebase và cập nhật lại danh sách
    private void deletePost(String postId, int position) {
        FirebaseDatabase.getInstance().getReference("posts").child(postId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
                    postList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, postList.size());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return postList.size(); // Trả về số lượng bài viết trong danh sách
    }

    @Override
    public long getItemId(int position) {
        return postList.get(position).getPostId().hashCode(); // Định danh duy nhất cho mỗi bài viết
    }

    // Cập nhật lại danh sách bài viết
    public void updatePosts(List<Post> newPosts) {
        postList.clear();
        if (newPosts != null) postList.addAll(newPosts);
        notifyDataSetChanged(); // Thông báo dữ liệu thay đổi để cập nhật giao diện
    }

    // ViewHolder đại diện cho mỗi item trong RecyclerView
    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagePost;
        Button btnDelete;
        TextView tvTagHighlight, tvViews, tvTitle, tvShortDesc, tvTimeAgo,
                tvContact, tvLocation, tvRole, tvPostDate, tvViewDetails;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagePost = itemView.findViewById(R.id.imagePost);
            tvTagHighlight = itemView.findViewById(R.id.tagHighlight);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvTitle = itemView.findViewById(R.id.titlePost);
            tvShortDesc = itemView.findViewById(R.id.shortDesc);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvPostDate = itemView.findViewById(R.id.tvPostDate);
            tvViewDetails = itemView.findViewById(R.id.tvViewDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

