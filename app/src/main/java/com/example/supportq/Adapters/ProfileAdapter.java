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
import com.example.supportq.Models.Question;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    public static final String TAG = "ProfileAdapter";
    private List<Question> allQuestions;
    private Context context;

    public ProfileAdapter(List<Question> allQuestions, Context context) {
        this.allQuestions = allQuestions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //listener for like button
        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Question question = allQuestions.get(position);
                boolean isLiked = question.isLiked();
                if (!isLiked) {
                    question.likePost(ParseUser.getCurrentUser());
                } else {
                    question.unlikePost(ParseUser.getCurrentUser());
                }
                question.saveInBackground();
                setButton(holder.ivLike, !isLiked, R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.likedRed);
                setLikeText(question, holder.tvLikeCount);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = allQuestions.get(position);
        try {
            holder.bind(question);
        } catch (Exception e) {
            Log.e(TAG, " error", e);
        }
    }

    @Override
    public int getItemCount() {
        return allQuestions.size();
    }

    // sets the color of a button, depending on whether it is active
    private void setButton(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(context, isActive ? activeColor : R.color.medium_gray));
    }

    private void setLikeText(Question question, TextView view) {
        int likeCount = question.getLikeCount();
        String itemsFound = context.getResources().getQuantityString(R.plurals.numberOfLikes, likeCount, likeCount);
        view.setText(itemsFound);
    }

    // Clean all elements of the recycler
    public void clear() {
        allQuestions.clear();
        notifyDataSetChanged();
    }

    // Add a list of post -- change to type used
    public void addAll(List<Question> list) {
        allQuestions.addAll(list);
        notifyDataSetChanged();
    }

    // Removes question at this position
    public void removeAt(Question question, int position) throws ParseException {
        allQuestions.remove(position);
        notifyItemRemoved(position);
        question.delete();
        question.saveInBackground();
    }

    //sets the comment count on the post
    private void setReplyText(Question question, TextView view) {
        int replyCount = question.getRepliesCount();
        String itemsFound = context.getResources().getQuantityString(R.plurals.numberOfReplies, replyCount, replyCount);
        view.setText(itemsFound);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        private ImageView ivLike;
        private TextView tvTimeStamp;
        private TextView tvLikeCount;
        private ImageView ivReply;
        private TextView tvUsername;
        private ImageView ivProfilePicture;
        private ImageView ivMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivReply = itemView.findViewById(R.id.ivReply);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivMedia = itemView.findViewById(R.id.ivMedia);
        }

        public void bind(Question question) throws Exception {
            String username = question.getUser().fetch().getUsername();
            tvDescription.setText(question.getDescription());
            String timeAgo = question.getCreatedTimeAgo();
            tvTimeStamp.setText(timeAgo);
            tvUsername.setText(username);
            ParseFile profilePhoto = question.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
            if (profilePhoto != null) {
                Glide.with(context).load(profilePhoto.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
            }
            ParseFile mediaImage = question.getImage();
            if (mediaImage != null) {
                Glide.with(context).load(mediaImage.getUrl()).transform(new RoundedCornersTransformation(R.dimen.RADIUS, R.dimen.MARGIN)).into(ivMedia);
            }else {
                ivMedia.setVisibility(View.GONE);
            }
            setButton(ivLike, question.isLiked(), R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
            setLikeText(question, tvLikeCount);
        }

        //listener for reply button
        public void replyButtonClicked() {
            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO --> enable reply
                }
            });
        }
    }
}
