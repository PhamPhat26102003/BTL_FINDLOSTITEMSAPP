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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;
    private OnPostClickListener onPostClickListener;

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

        holder.tvViews.setText("👁 1 lượt xem"); // Cần cải thiện nếu có trường views

        long currentTime = System.currentTimeMillis();
        long postTime = post.getPostDate() != 0 ? post.getPostDate() : currentTime; // Kiểm tra giá trị postDate, nếu là 0 thì dùng currentTime

// Tính sự chênh lệch thời gian (số ngày đã trôi qua)
        long diffInMillis = currentTime - postTime;
        long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        holder.tvTimeAgo.setText("🕒 " + daysDiff + " ngày trước");

// Hiển thị thông tin liên lạc, địa điểm và vai trò
        holder.tvContact.setText("📞 " + post.getContactInfo());
        holder.tvLocation.setText("📍 " + post.getLocation());
        holder.tvRole.setText("👤 Quản trị viên"); // Cần cải thiện nếu có trường role

// Hiển thị ngày đăng bài dưới định dạng yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.tvPostDate.setText("📅 " + dateFormat.format(new Date(post.getPostDate() != 0 ? post.getPostDate() : currentTime)));

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
        Log.d("PostAdapter", "Cập nhật danh sách bài đăng, kích thước: " + newPosts.size());
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