package com.example.supportq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.supportq.Models.OnDoubleTapListener;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.TextViewAnimationOnClick;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    public interface OnEditIconClicked {
        void onQuestionClicked(int position);
    }

    public static final String TAG = "ProfileAdapter";
    private List<Question> allQuestions;
    private Context context;
    private OnEditIconClicked onEditIconClicked;

    public ProfileAdapter(List<Question> allQuestions, Context context, OnEditIconClicked onEditIconClicked) {
        this.allQuestions = allQuestions;
        this.context = context;
        this.onEditIconClicked = onEditIconClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.personal_post, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.onImageDoubleTap();
        holder.heartIconClicked();
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
    public void addAll(List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).getIsDeleted() && !questions.get(i).didUserHideQuestion()) {
                allQuestions.add(questions.get(i));
            }
        }
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ExpandableTextView tvDescription;
        private ImageView ivLike;
        private TextView tvTimeStamp;
        private TextView tvLikeCount;
        private TextView tvUsername;
        private ImageView ivProfilePicture;
        private ImageView ivMedia;
        private ImageView ivEditIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivEditIcon = itemView.findViewById(R.id.ivEditIcon);
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
            } else {
                ivProfilePicture.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            }
            ParseFile mediaImage = question.getImage();
            if (mediaImage != null) {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(mediaImage.getUrl()).placeholder(R.drawable.placeholder).into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
            setButton(ivLike, question.isLiked(), R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
            setLikeText(question, tvLikeCount);
            editIconClicked();
            TextViewAnimationOnClick.onQuestionClickAnimation(tvDescription, allQuestions, getAdapterPosition());
            tvDescription.resetState(question.isShrink());
        }

        //listener for edit icon
        public void editIconClicked() {
            ivEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onEditIconClicked.onQuestionClicked(getAdapterPosition());
                }
            });
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
