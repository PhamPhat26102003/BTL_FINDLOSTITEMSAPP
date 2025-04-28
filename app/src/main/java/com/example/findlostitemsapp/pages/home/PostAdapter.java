package com.example.findlostitemsapp.pages.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.findlostitemsapp.R;
import com.example.findlostitemsapp.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;
    private OnPostClickListener onPostClickListener;
    private HashMap<String, String> userNameCache;
    private DatabaseReference usersRef;
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd HH:mm:ss",
            "dd/MM/yyyy"
    };

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public PostAdapter(Context context, List<Post> postList, OnPostClickListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.postList = postList != null ? postList : new ArrayList<>();
        this.onPostClickListener = listener;
        this.userNameCache = new HashMap<>();
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        if (post == null) {
            Log.w("PostAdapter", "Post tại vị trí " + position + " là null");
            return;
        }

        holder.tvTitle.setText(post.getTitle());
        holder.tvShortDesc.setText(post.getDescription());
        Log.d("AdapterBinding", "Hiển thị bài đăng: " + post.getTitle());

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

        if (post.getTag() != null && post.getTag().equals("Tình trạng cấp")) {
            holder.tvTagHighlight.setVisibility(View.VISIBLE);
            holder.tvTagHighlight.setText("Tin nổi bật");
        } else {
            holder.tvTagHighlight.setVisibility(View.GONE);
        }

        holder.tvViews.setText("👁 " + post.getViewCount() + " lượt xem");

        // Xử lý postDate với nhiều định dạng
        long currentTime = System.currentTimeMillis();
        String postDateStr = post.getPostDate();
        long postTime = currentTime;
        Date postDate = null;

        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                postDate = sdf.parse(postDateStr);
                if (postDate != null) {
                    postTime = postDate.getTime();
                    break;
                }
            } catch (ParseException e) {
                Log.w("PostAdapter", "Không thể phân tích postDate: " + postDateStr + " với định dạng: " + format);
            }
        }

        if (postDate == null) {
            Log.e("PostAdapter", "Không thể phân tích postDate: " + postDateStr + ", sử dụng ngày hiện tại");
            postDate = new Date();
        }

        long diffInMillis = currentTime - postTime;
        long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (daysDiff == 0) {
            holder.tvTimeAgo.setText("🕒 Hôm nay");
        } else if (daysDiff == 1) {
            holder.tvTimeAgo.setText("🕒 1 ngày trước");
        } else {
            holder.tvTimeAgo.setText("🕒 " + daysDiff + " ngày trước");
        }

        holder.tvContact.setText("📞 " + post.getContactInfo());
        holder.tvLocation.setText("📍 " + post.getLocation());

        // Lấy và hiển thị tên người dùng
        String userId = post.getUserId();
        if (userId == null || userId.isEmpty()) {
            holder.tvRole.setText("👤 Người dùng ẩn danh");
            Log.w("PostAdapter", "userId null hoặc rỗng cho bài đăng: " + post.getPostId());
        } else if (userNameCache.containsKey(userId)) {
            holder.tvRole.setText("👤 " + userNameCache.get(userId));
        } else {
            usersRef.child(userId).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName = snapshot.getValue(String.class);
                    if (userName != null && !userName.isEmpty()) {
                        userNameCache.put(userId, userName);
                        holder.tvRole.setText("👤 " + userName);
                    } else {
                        userNameCache.put(userId, "Người dùng ẩn danh");
                        holder.tvRole.setText("👤 Người dùng ẩn danh");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("PostAdapter", "Lỗi tải userName cho userId: " + userId, error.toException());
                    userNameCache.put(userId, "Người dùng ẩn danh");
                    holder.tvRole.setText("👤 Người dùng ẩn danh");
                }
            });
        }

        // Hiển thị ngày đăng bài
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.tvPostDate.setText("📅 " + dateFormat.format(postDate));

        holder.itemView.setOnClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onPostClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public long getItemId(int position) {
        return postList.get(position).getPostId().hashCode();
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList.clear();
        if (newPosts != null) {
            this.postList.addAll(newPosts);
        }
        Log.d("PostAdapter", "Cập nhật danh sách bài đăng, kích thước: " + postList.size());
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagePost;
        TextView tvTagHighlight, tvViews, tvTitle, tvShortDesc, tvTimeAgo, tvContact, tvLocation, tvRole, tvPostDate, tvViewDetails;

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
        }
    }
}