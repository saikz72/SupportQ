package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Adapters.ReplyAdapter;
import com.example.supportq.Fragments.HomeFragment;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.Reply;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

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
    private SwipeRefreshLayout swipeContainer;
    private EditText etReply;
    private RecyclerView rvQuestions;
    private ImageView ivSendReply;
    private ReplyAdapter replyAdapter;
    private Question question;
    private ParseUser currentUser;
    private List<Reply> allReplies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        setViews();
        currentUser = ParseUser.getCurrentUser();
        allReplies = new ArrayList<>();
        replyAdapter = new ReplyAdapter(allReplies, this);
        rvQuestions.setAdapter(replyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvQuestions.setLayoutManager(linearLayoutManager);
        try {
            bind();
        } catch (Exception e) {
        }
        setButton(ivLike, question.isLiked(),
                R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.likedRed);
        setLikeText(question, tvLikeCount);
        likeIconClicked();
        replyListener();
        queryReply();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryReply();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    public void setViews() {
        rvQuestions =findViewById(R.id.rvQuestions);
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
        ivSendReply = findViewById(R.id.ivSendReply);
        etReply = findViewById(R.id.etReply);
        swipeContainer = findViewById(R.id.swipeContainer);
    }

    public void replyListener(){
        ivSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reply reply = new Reply();
                reply.setReply(etReply.getText().toString());
                reply.setUser(currentUser);
                reply.setQuestionId(question);
                reply.saveInBackground();
                etReply.setText("");
                queryReply();
            }
        });
    }

    private void queryReply() {
        final ParseQuery<Reply> query = ParseQuery.getQuery(Reply.class);
        query.addDescendingOrder(Reply.KEY_CREATED_AT); //TODO --> complex sorting algorithm
        query.whereEqualTo(Reply.KEY_QUESTION_ID, question);
        query.findInBackground(new FindCallback<Reply>() {
            @Override
            public void done(List<Reply> replies, ParseException e) {
                if(e != null){
                    Toast.makeText(QuestionDetailsActivity.this, LoginActivity.TOAST_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    return;
                }
                question.setReplies(replies);
                question.saveInBackground();
                replyAdapter.clear();
                replyAdapter.addAll(replies);
                setReplyText(question, tvReplyCount);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, new Intent());
        finish();
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

    //sets the comment count on the post
    private void setReplyText(Question question, TextView view){
        int replyCount = question.getRepliesCount();
        if(replyCount == 1)
            view.setText(String.format("%d reply", replyCount));
        else
            view.setText(String.format("%d replies", replyCount));
    }

}