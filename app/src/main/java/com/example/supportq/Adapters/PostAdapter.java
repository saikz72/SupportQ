package com.example.supportq.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supportq.Models.Post;
import com.example.supportq.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> allPosts;
    private Context context;

    public PostAdapter(List<Post> allPosts, Context context) {
        this.allPosts = allPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = allPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return allPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(Post post) {
            tvDescription.setText(post.getDescription());
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        allPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of post -- change to type used
    public void addAll(List<Post> list) {
        allPosts.addAll(list);
        notifyDataSetChanged();
    }
}
