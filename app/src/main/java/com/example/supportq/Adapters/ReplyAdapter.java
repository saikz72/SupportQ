package com.example.supportq.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Models.Reply;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    public static final String INSTRUCTOR_ID = "70DSaTvf";
    public static final String TA_ID = "axB9wBpruP";
    private List<Reply> allReplies;
    private Context context;
    private ParseUser currentUser;

    public ReplyAdapter(List<Reply> allReplies, Context context) {
        this.allReplies = allReplies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        currentUser = ParseUser.getCurrentUser();
        final ReplyAdapter.ViewHolder holder = new ReplyAdapter.ViewHolder(view);
        holder.ivVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Reply reply = allReplies.get(position);
                if (currentUser.getObjectId().equals(INSTRUCTOR_ID) || currentUser.getObjectId().equals(TA_ID)) {
                    reply.setIsApproved(!reply.getIsApproved());
                    reply.saveInBackground();
                    setVerificationButton(holder.ivVerify, reply.getIsApproved(), R.drawable.very_inactive, R.drawable.verify_activie, R.color.green);
                }
            }
        });
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

    // sets the color of a button, depending on whether it is active
    private void setVerificationButton(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(context, isActive ? activeColor : R.color.medium_gray));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvTimeStamp;
        private TextView tvReply;
        private ImageView ivProfilePicture;
        private ImageView ivVerify;
        private ImageView ivDelete;
        private TextView tvApproveNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvReply = itemView.findViewById(R.id.tvReply);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivVerify = itemView.findViewById(R.id.ivVerify);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvApproveNote = itemView.findViewById(R.id.tvApproveNote);
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
            setVerificationButton(ivVerify, reply.getIsApproved(), R.drawable.very_inactive, R.drawable.verify_activie, R.color.green);
            if (reply.getIsApproved()) {
                tvApproveNote.setVisibility(View.VISIBLE);
            }
            if (reply.getUser().getObjectId().equals(currentUser.getObjectId())) {
                ivDelete.setVisibility(View.VISIBLE);
            }
        }
    }
}
