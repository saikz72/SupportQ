package com.example.supportq.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Activities.QuestionDetailsActivity;
import com.example.supportq.Models.OnDoubleTapListener;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    public static final String TAG = "QuestionAdapter";
    public static final int REQUEST_CODE = 200;
    private List<Question> allQuestions;
    private Context context;
    private Activity activity;

    public QuestionAdapter(List<Question> allQuestions, Context context, Activity activity) {
        this.allQuestions = allQuestions;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.onImageDoubleTap();
        //listener for like button
        holder.heartIconClicked();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = allQuestions.get(position);
        try {
            holder.bind(question);
        } catch (Exception e) {
            Log.e(TAG, "error", e);
            return;
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

    //sets the comment count on the post
    private void setReplyText(Question question, TextView view) {
        int replyCount = question.getRepliesCount();
        String itemsFound = context.getResources().getQuantityString(R.plurals.numberOfReplies, replyCount, replyCount);
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

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDescription;
        private ImageView ivLike;
        private TextView tvTimeStamp;
        private TextView tvLikeCount;
        private ImageView ivReply;
        private TextView tvUsername;
        private ImageView ivMedia;
        private ImageView ivProfilePicture;
        private TextView tvReplyCount;
        private ImageView ivEditIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivReply = itemView.findViewById(R.id.ivReply);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            ivEditIcon = itemView.findViewById(R.id.ivEditIcon);
            itemView.setOnClickListener(this);
            ivReply.setOnClickListener(this);
        }

        public void bind(Question question) {
            String username = question.getUser().getUsername();
            tvDescription.setText(question.getDescription());
            ParseFile profilePhoto = question.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
            if (profilePhoto != null) {
                Glide.with(context).load(profilePhoto.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
            }
            ParseFile mediaImage = question.getImage();
            if (mediaImage != null) {
                Glide.with(context).load(mediaImage.getUrl()).transform(new RoundedCornersTransformation(R.dimen.RADIUS, R.dimen.MARGIN)).into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
            tvUsername.setText(username);
            String timeAgo = question.getCreatedTimeAgo();
            tvTimeStamp.setText(timeAgo);
            setButton(ivLike, question.isLiked(), R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
            setLikeText(question, tvLikeCount);
            setReplyText(question, tvReplyCount);
            ivEditIcon.setVisibility(View.GONE);
        }

        //imageview double tap to like handler
        @SuppressLint("ClickableViewAccessibility")
        public void onImageDoubleTap() {
            ivMedia.setOnTouchListener(new OnDoubleTapListener(context) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    int position = getAdapterPosition();
                    Question question = allQuestions.get(position);
                    boolean isLiked = question.isLiked();
                    if (!isLiked) {
                        question.likePost(ParseUser.getCurrentUser());
                    } else {
                        question.unlikePost(ParseUser.getCurrentUser());
                    }
                    question.saveInBackground();
                    setButton(ivLike, !isLiked, R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
                    setLikeText(question, tvLikeCount);
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, QuestionDetailsActivity.class);
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                Question question = allQuestions.get(getAdapterPosition());
                intent.putExtra(String.valueOf(R.string.HOME_FRAGMENT_KEY), Parcels.wrap(question));
                activity.startActivityForResult(intent, REQUEST_CODE);
            }
        }

        //heart icon single tap to like handler
        public void heartIconClicked() {
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Question question = allQuestions.get(position);
                    boolean isLiked = question.isLiked();
                    if (!isLiked) {
                        question.likePost(ParseUser.getCurrentUser());
                    } else {
                        question.unlikePost(ParseUser.getCurrentUser());
                    }
                    question.saveInBackground();
                    setButton(ivLike, !isLiked, R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
                    setLikeText(question, tvLikeCount);
                }
            });
        }
    }
}
