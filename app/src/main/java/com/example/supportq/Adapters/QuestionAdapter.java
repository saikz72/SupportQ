package com.example.supportq.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Activities.QuestionDetailsActivity;
import com.example.supportq.Models.OnDoubleTapListener;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.TextViewAnimationOnClick;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.embersoft.expandabletextview.ExpandableTextView;

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
        holder.heartIconClicked();
        holder.bookMarkQuestion();
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

    private void setBookMarkButtonColor(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(context, isActive ? activeColor : R.color.black));
    }

    private void setSahpeOfHiddenIcon(ImageView iv, boolean isActive, int strokeResId, int fillResId) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
    }

    private void setTextOfHideIcon(Question question, TextView view) {
        boolean isHidden = question.didUserHideQuestion();
        if (isHidden) {
            view.setText(context.getString(R.string.unhide));
        } else {
            view.setText(context.getString(R.string.hide));
        }
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

    //add trending post to the adapter if they exist
    public boolean addTrendingPost(List<Question> questions) {
        List<Question> list = addQuestionsOverPastDay(questions);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLikeCount() > 1 && list.get(i).getRepliesCount() > 1) {
                allQuestions.add(list.get(i));
            }
        }
        notifyDataSetChanged();
        if (allQuestions.size() > 0) {
            return true;
        }
        return false;
    }

    //add post within 24 hours -- TODO --> CHANGE TIME FRAME
    public List<Question> addQuestionsOverPastDay(List<Question> questions) {
        Date currentDate = Calendar.getInstance().getTime();
        List<Question> list = new ArrayList<>();
        int day = 24;
        for (int i = 0; i < questions.size(); i++) {
            Date dateOfPost = questions.get(i).getDate();
            if (timeDifference(dateOfPost, currentDate) < day) {
                list.add(questions.get(i));
            }
        }
        return list;
    }

    public long timeDifference(Date startDate, Date endDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        try {
            startDate = df.parse(startDate.toString());
            endDate = df.parse(endDate.toString());
        } catch (java.text.ParseException e) {
        }
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long elapsedHours = different / hoursInMilli;
        return elapsedHours;
    }

    // Add a list of post -- change to type used
    public void addAll(List<Question> questions) {
        //mergeSort(questions);
        List<Question> unDeletedQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).getIsDeleted() && !questions.get(i).didUserHideQuestion()) {
                unDeletedQuestions.add(questions.get(i));
            }
        }
        allQuestions.addAll(mergeSort(unDeletedQuestions));
        notifyDataSetChanged();
    }

    public void addHiddenPostByUser(List<Question> questions) {
        List<Question> hiddenPost = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).getIsDeleted() && questions.get(i).didUserHideQuestion()) {
                hiddenPost.add(questions.get(i));
            }
        }
        allQuestions.addAll(hiddenPost);
        notifyDataSetChanged();
    }

    public void addBookMarkedQuestions(List<Question> questions) {
        List<Question> bookmarkedPost = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).getIsDeleted() && questions.get(i).isBookMarked()) {
                bookmarkedPost.add(questions.get(i));
            }
        }
        allQuestions.addAll(bookmarkedPost);
        notifyDataSetChanged();
    }

    //mergesort
    public List<Question> mergeSort(List<Question> questions) {
        if (questions.size() <= 1) {
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
        private ImageView ivHide;
        private ImageView ivHeart;
        private ImageView ivBookMark;
        private TextView tvHide;
        private ImageView ivQuestionMark;
        private AnimatedVectorDrawable avd2;
        private AnimatedVectorDrawableCompat avd;

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
            ivHide = itemView.findViewById(R.id.ivHide);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            ivBookMark = itemView.findViewById(R.id.ivBookmark);
            tvHide = itemView.findViewById(R.id.tvHide);
            ivQuestionMark = itemView.findViewById(R.id.ivQuestionMark);
            itemView.setOnClickListener(this);
            ivReply.setOnClickListener(this);
        }

        public void bind(Question question) {
            bindTextViews(question);
            ParseFile profilePhoto = question.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
            if (profilePhoto != null) {
                Glide.with(context).load(profilePhoto.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
            } else {
                ivProfilePicture.setImageResource(R.drawable.profile_image_default);
            }
            ParseFile mediaImage = question.getImage();
            if (mediaImage != null) {
                setImageOfPost(mediaImage);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
            if (question.getRepliesCount() > 0) {
                ivQuestionMark.setImageResource(R.drawable.question_answered);
            } else {
                ivQuestionMark.setImageResource(R.drawable.question_mark);
            }
            setButton(ivLike, question.isLiked(), R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
            setBookMarkButtonColor(ivBookMark, question.isBookMarked(), R.drawable.ic_baseline_bookmark_border_24, R.drawable.ic_baseline_bookmark_24, R.color.black);
            hideIconClicked();
            setSahpeOfHiddenIcon(ivHide, question.didUserHideQuestion(), R.drawable.ic_outline_remove_red_eye_24, R.drawable.ic_outline_visibility_off_24);
            setTextOfHideIcon(question, tvHide);
        }

        //binding text with textviews
        public void bindTextViews(Question question) {
            String username = question.getUser().getUsername();
            tvUsername.setText(username);
            String timeAgo = question.getCreatedTimeAgo();
            tvTimeStamp.setText(timeAgo);
            setLikeText(question, tvLikeCount);
            setReplyText(question, tvReplyCount);
            TextViewAnimationOnClick.onQuestionClickAnimation(tvDescription, allQuestions, getAdapterPosition());
            tvDescription.setText(question.getDescription());
            tvDescription.resetState(question.isShrink());
        }

        // sets the imageview of post
        public void setImageOfPost(ParseFile mediaImage) {
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(context).load(mediaImage.getUrl()).placeholder(R.drawable.placeholder).into(ivMedia);
        }

        //imageview double tap to like handler
        @SuppressLint("ClickableViewAccessibility")
        public void onImageDoubleTap() {
            ivMedia.setOnTouchListener(new OnDoubleTapListener(context) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    showHeartAnimation();
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

        //animate heart on double tap
        public void showHeartAnimation() {
            ivHeart.setVisibility(View.VISIBLE);
            final Drawable drawable = ivHeart.getDrawable();
            ivHeart.setAlpha(0.90f);
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                avd = (AnimatedVectorDrawableCompat) drawable;
                avd.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                avd2 = (AnimatedVectorDrawable) drawable;
                avd2.start();
            }
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
        public void hideIconClicked() {
            ivHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Question question = allQuestions.get(getAdapterPosition());
                    boolean isHidden = question.didUserHideQuestion();
                    if (isHidden) {
                        question.setQuestionVisibleToUser(ParseUser.getCurrentUser());
                        notifyDataSetChanged();
                    } else {
                        question.setQuestionToHidden(ParseUser.getCurrentUser());
                    }
                    allQuestions.remove(question);
                    notifyDataSetChanged();
                    question.saveInBackground();
                    setSahpeOfHiddenIcon(ivHide, !isHidden, R.drawable.ic_outline_remove_red_eye_24, R.drawable.ic_outline_visibility_off_24);
                }
            });
        }

        //bookmark icon handler
        public void bookMarkQuestion() {
            ivBookMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Question question = allQuestions.get(position);
                    boolean isBookMarked = question.isBookMarked();
                    if (!isBookMarked) {
                        question.bookMarkQuestion(ParseUser.getCurrentUser());
                    } else {
                        question.unBookMarkQuestion(ParseUser.getCurrentUser());
                    }
                    question.saveInBackground();
                    setBookMarkButtonColor(ivBookMark, !isBookMarked, R.drawable.ic_baseline_bookmark_border_24, R.drawable.ic_baseline_bookmark_24, R.color.black);
                }
            });
        }
    }
}
