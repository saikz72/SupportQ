package com.example.supportq.Adapters;

import android.content.Context;
import android.util.Log;
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
        holder.verifyReply();
        holder.deleteResponse();
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

    // Removes reply at this position
    public void removeAt(Reply reply, int position) throws ParseException {
        allReplies.remove(position);
        notifyItemRemoved(position);
        reply.delete();
        reply.saveInBackground();
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
            verificationStatus(reply);
            deleteIconVisibility(reply);
            setVerificationButton(ivVerify, reply.getIsApproved(), R.drawable.very_inactive, R.drawable.verify_activie, R.color.green);
            tvTimeStamp.setText(reply.getCreatedTimeAgo());
            tvReply.setText(reply.getReply());
            try {
                tvUsername.setText(reply.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
            }
            ParseFile profilePhoto = reply.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
            if (profilePhoto != null)
                Glide.with(context).load(profilePhoto.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
            if(User.getIsInstructor(currentUser))
                ivVerify.setVisibility(View.VISIBLE);
        }

        //checks whether response is verified by instructor
        public void verificationStatus(Reply reply) {
            if (reply.getIsApproved()){
                tvApproveNote.setVisibility(View.VISIBLE);
                ivVerify.setVisibility(View.VISIBLE);
            }
            else if(!reply.getIsApproved())
                tvApproveNote.setVisibility(View.INVISIBLE);
        }

        //shows delete icon for current user's replies
        public void deleteIconVisibility(Reply reply){
            if (reply.getUser().getObjectId().equals(currentUser.getObjectId())){
                ivDelete.setVisibility(View.VISIBLE);
            }
            else{
                ivDelete.setVisibility(View.INVISIBLE);
            }
        }

        //method for approving a response by an instructor
        public void verifyReply() {
            ivVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Reply reply = allReplies.get(position);
                    if (User.getIsInstructor(currentUser)) {
                        reply.setIsApproved(!reply.getIsApproved());
                        reply.saveInBackground();
                        setVerificationButton(ivVerify, reply.getIsApproved(), R.drawable.very_inactive, R.drawable.verify_activie, R.color.green);
                        verificationStatus(reply);
                    }
                }
            });
        }

        //method for deleting a response by a user
        public void deleteResponse() {
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    try {
                        removeAt(allReplies.get(position), position);
                    } catch (ParseException e) {
                        Log.e("TAG", "erro", e);
                    }
                }
            });
        }
    }
}
