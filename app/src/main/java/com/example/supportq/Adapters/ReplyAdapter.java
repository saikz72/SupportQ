package com.example.supportq.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Models.Reply;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private List<Reply> allReplies;
    private Context context;

    public ReplyAdapter(List<Reply> allReplies, Context context) {
        this.allReplies = allReplies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        final ReplyAdapter.ViewHolder holder = new ReplyAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reply reply = allReplies.get(position);
        holder.bind(reply);
    }

    @Override
    public int getItemCount() {
        return allReplies.size();
    }
    // Clean all elements of the recycler
    public void clear() {
        allReplies.clear();
        notifyDataSetChanged();
    }

    // Add a list of post -- change to type used
    public void addAll(List<Reply> list) {
        allReplies.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvTimeStamp;
        private TextView tvReply;
        private ImageView ivProfilePicture;
        private ImageView ivVerify;
        private ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvReply = itemView.findViewById(R.id.tvReply);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivVerify = itemView.findViewById(R.id.ivVerify);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }

        public void bind(Reply reply) {
            tvTimeStamp.setText(reply.getCreatedTimeAgo());
            try {
                tvUsername.setText(reply.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) { }
            tvReply.setText(reply.getReply());
            ParseFile profilePhoto = reply.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
            if (profilePhoto != null)
                Glide.with(context).load(profilePhoto.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
        }
    }
}
