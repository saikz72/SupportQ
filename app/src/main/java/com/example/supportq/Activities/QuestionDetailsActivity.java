package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Fragments.HomeFragment;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class QuestionDetailsActivity extends AppCompatActivity {
    private TextView tvUsername;
    private TextView tvFullname;
    private TextView tvReplyCount;
    private TextView tvLikeCount;
    private TextView tvDescription;
    private TextView tvTimeStamp;
    private ImageView ivReply;
    private ImageView ivLike;
    private ImageView ivDelete;
    private ImageView ivProfilePicture;
    private Question question;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        currentUser = ParseUser.getCurrentUser();
        setViews();
        try {
            bind();
        } catch (Exception e) {
        }
        setButton(ivLike, question.isLiked(),
                R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.likedRed);
        setLikeText(question, tvLikeCount);
        likeIconClicked();
    }

    public void setViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvFullname = findViewById(R.id.tvFullname);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvReplyCount = findViewById(R.id.tvReplyCount);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        ivDelete = findViewById(R.id.ivDelete);
        ivLike = findViewById(R.id.ivLike);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivReply = findViewById(R.id.ivReply);
    }

    public void bind() throws Exception {
        question = Parcels.unwrap(getIntent().getParcelableExtra(HomeFragment.HOME_FRAGMENT_KEY));
        tvTimeStamp.setText(question.getCreatedTimeAgo());
        tvUsername.setText("@" + question.getUser().getUsername());
        tvFullname.setText(User.getFullName(question.getUser()));
        ParseFile profileImage = question.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
        if (profileImage != null)
            Glide.with(this).load(profileImage.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
        tvDescription.setText(question.getDescription());

    }

    //listener for like button
    public void likeIconClicked() {
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLiked = question.isLiked();
                if (!isLiked)
                    question.likePost(currentUser);
                else
                    question.unlikePost(currentUser);
                question.saveInBackground();
                setButton(ivLike, !isLiked, R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
                setLikeText(question, tvLikeCount);
            }
        });
    }

    // sets the color of a button, depending on whether it is active
    private void setButton(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(this, isActive ? activeColor : R.color.medium_gray));
    }

    private void setLikeText(Question post, TextView view) {
        int likeCount = post.getLikeCount();
        if (likeCount == 1) view.setText(String.format("%d like", post.getLikeCount()));
        else view.setText(String.format("%d Likes", post.getLikeCount()));
    }

}