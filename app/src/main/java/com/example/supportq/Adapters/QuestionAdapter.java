package com.example.supportq.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.supportq.Activities.QuestionDetailsActivity;
import com.example.supportq.Models.OnDoubleTapListener;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.TextViewAnimationOnClick;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    public interface OnHideIconClicked{
        void hidePostFromTimeline(int position);
    }

    public static final String TAG = "QuestionAdapter";
    public static final int REQUEST_CODE = 200;
    private List<Question> allQuestions;
    private Context context;
    private Activity activity;
    private OnHideIconClicked onHideIconClicked;

    public QuestionAdapter(List<Question> allQuestions, Context context, Activity activity, OnHideIconClicked onHideIconClicked) {
        this.allQuestions = allQuestions;
        this.context = context;
        this.activity = activity;
        this.onHideIconClicked = onHideIconClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.onImageDoubleTap();
        holder.heartIconClicked();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Question question = allQuestions.get(position);
        holder.bind(question);
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
    public void addAll(List<Question> questions) {
        //mergeSort(questions);
        List<Question> unDeletedQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).getIsDeleted()) {
                unDeletedQuestions.add(questions.get(i));
            }
        }
        allQuestions.addAll(mergeSort(unDeletedQuestions));
        notifyDataSetChanged();
    }

    //mergesort
    public List<Question> mergeSort(List<Question> questions) {
        if (questions.size() == 1) {
            return questions;
        }
        int midPoint = (questions.size() - 1) / 2;
        List<Question> partition1 = new ArrayList<>();
        List<Question> partition2 = new ArrayList<>();
        for (int i = 0; i <= midPoint; i++) {
            partition1.add(questions.get(i));
        }
        for (int i = midPoint + 1; i < questions.size(); i++) {
            partition2.add(questions.get(i));
        }
        partition1 = mergeSort(partition1);
        partition2 = mergeSort(partition2);
        return merge(partition1, partition2);
    }

    private List<Question> merge(List<Question> partition1, List<Question> partition2) {
        List<Question> questions = new ArrayList<>();
        while (!partition1.isEmpty() && !partition2.isEmpty()) {
            if (partition1.get(0).compareTo(partition2.get(0)) < 0) {       //second one bigger
                questions.add(partition2.remove(0));
            } else if (partition1.get(0).compareTo(partition2.get(0)) > 0) {  //first one bigger
                questions.add(partition1.remove(0));
            }
        }
        while (!partition1.isEmpty()) {
            questions.add(partition1.remove(0));
        }
        while (!partition2.isEmpty()) {
            questions.add(partition2.remove(0));
        }
        return questions;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ExpandableTextView tvDescription;
        private ImageView ivLike;
        private TextView tvTimeStamp;
        private TextView tvLikeCount;
        private ImageView ivReply;
        private TextView tvUsername;
        private ImageView ivMedia;
        private ImageView ivProfilePicture;
        private TextView tvReplyCount;
        private ProgressBar pbLoading;
        private ImageView ivHide;

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
            pbLoading = itemView.findViewById(R.id.pbLoading);
            ivHide = itemView.findViewById(R.id.ivHide);
            itemView.setOnClickListener(this);
            ivReply.setOnClickListener(this);
        }

        public void bind(Question question) {
            String username = question.getUser().getUsername();
            ParseFile profilePhoto = question.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
            if (profilePhoto != null) {
                Glide.with(context).load(profilePhoto.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
            } else {
                ivProfilePicture.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            }
            ParseFile mediaImage = question.getImage();
            if (mediaImage != null) {
                setImageOfPost(mediaImage);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
            tvUsername.setText(username);
            String timeAgo = question.getCreatedTimeAgo();
            tvTimeStamp.setText(timeAgo);
            setButton(ivLike, question.isLiked(), R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
            setLikeText(question, tvLikeCount);
            setReplyText(question, tvReplyCount);
            TextViewAnimationOnClick.onQuestionClickAnimation(tvDescription, allQuestions, getAdapterPosition());
            tvDescription.setText(question.getDescription());
            tvDescription.resetState(question.isShrink());
            hideIconClicked();
        }

        // sets the imageview of post
        public void setImageOfPost(ParseFile mediaImage) {
            pbLoading.setVisibility(View.VISIBLE);
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(context).load(mediaImage.getUrl()).placeholder(R.drawable.placeholder).
                    listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            pbLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pbLoading.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(ivMedia);
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

        //hide icon handler
        public void hideIconClicked(){
            ivHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   onHideIconClicked.hidePostFromTimeline(getAdapterPosition());
                }
            });
        }
    }
}
