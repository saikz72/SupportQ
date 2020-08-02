package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Adapters.ReplyAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.Reply;
import com.example.supportq.Models.TextViewAnimationOnClick;
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

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ru.embersoft.expandabletextview.ExpandableTextView;

public class QuestionDetailsActivity extends AppCompatActivity {
    private TextView tvUsername;
    private TextView tvFullname;
    private TextView tvReplyCount;
    private TextView tvLikeCount;
    private ExpandableTextView tvDescription;
    private TextView tvTimeStamp;
    private ImageView ivReply;
    private ImageView ivLike;
    private ImageView ivProfilePicture;
    private SwipeRefreshLayout swipeContainer;
    private EditText etReply;
    private RecyclerView rvQuestions;
    private ImageView ivSendReply;
    private ImageView ivMedia;
    private SwitchCompat switchCompat;
    private ReplyAdapter replyAdapter;
    private Question question;
    private ParseUser currentUser;
    private List<Reply> allReplies;
    private boolean isToggleEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        setViews();
        currentUser = ParseUser.getCurrentUser();
        setUpRecyclerView();
        try {
            bind();
        } catch (ParseException e) {
            Log.e("TAG", "ParseException", e);
        }
        setButton(ivLike, question.isLiked(), R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
        setLikeText(question, tvLikeCount);
        likeIconClicked();
        replyListener();
        queryReply();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isToggleEnable) {
                    queryOnlyVerifiedResponse();
                } else {
                    queryReply();
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        setUpForVerifiedResponses();
    }

    public void setUpRecyclerView() {
        allReplies = new ArrayList<>();
        ReplyAdapter.OnDeleteIconListener onDeleteIconListener = new ReplyAdapter.OnDeleteIconListener() {
            @Override
            public void deleteReply(int position) {
                Reply reply = allReplies.get(position);
                replyAdapter.notifyDataSetChanged();
                allReplies.remove(position);
                try {
                    reply.delete();
                } catch (ParseException e) {
                    Log.e("TAG", "ParseException", e);
                }
                reply.saveInBackground();
                queryReply();
                setReplyText(question, tvReplyCount);
            }
        };
        replyAdapter = new ReplyAdapter(allReplies, this, onDeleteIconListener);
        rvQuestions.setAdapter(replyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvQuestions.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        rvQuestions.addItemDecoration(divider);
    }

    public void setViews() {
        rvQuestions = findViewById(R.id.rvQuestions);
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvFullname = findViewById(R.id.tvFullname);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvReplyCount = findViewById(R.id.tvReplyCount);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        ivLike = findViewById(R.id.ivLike);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivReply = findViewById(R.id.ivReply);
        ivSendReply = findViewById(R.id.ivSendReply);
        etReply = findViewById(R.id.etReply);
        swipeContainer = findViewById(R.id.swipeContainer);
        ivMedia = findViewById(R.id.ivMedia);
        switchCompat = findViewById(R.id.switchCompat);
    }

    public void replyListener() {
        ivSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reply reply = new Reply();
                if (!etReply.getText().toString().isEmpty() && etReply.getText().toString().trim().length() > 0) {
                    reply.setReply(etReply.getText().toString());
                    reply.setUser(currentUser);
                    reply.setQuestionId(question);
                    reply.saveInBackground();
                    etReply.setText("");
                    queryReply();
                } else {
                    etReply.setError(getString(R.string.reply_error));
                }
            }
        });
    }

    private void queryReply() {
        final ParseQuery<Reply> query = ParseQuery.getQuery(Reply.class);
        query.addDescendingOrder(Reply.KEY_CREATED_AT);
        query.whereEqualTo(Reply.KEY_QUESTION_ID, question);
        query.findInBackground(new FindCallback<Reply>() {
            @Override
            public void done(List<Reply> replies, ParseException e) {
                if (e != null) {
                    Toast.makeText(QuestionDetailsActivity.this, getString(R.string.TOAST_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
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

    public void bind() throws ParseException {
        question = Parcels.unwrap(getIntent().getParcelableExtra(String.valueOf(R.string.HOME_FRAGMENT_KEY)));
        tvTimeStamp.setText(question.getCreatedTimeAgo());
        tvUsername.setText("@" + question.getUser().fetchIfNeeded().getUsername());
        tvFullname.setText(User.getFullName(question.getUser()));
        ParseFile profileImage = question.getUser().getParseFile(User.KEY_PROFILE_PICTURE);
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
        }
        ParseFile mediaImage = question.getImage();
        if (mediaImage != null) {
            Glide.with(this).load(mediaImage.getUrl()).transform(new RoundedCornersTransformation(R.dimen.RADIUS, R.dimen.MARGIN)).into(ivMedia);
        } else {
            ivMedia.setVisibility(View.GONE);
        }
        tvDescription.setText(question.getDescription());
        TextViewAnimationOnClick.onQuestionClickedInDetailScreen(tvDescription, question);
        tvDescription.resetState(question.isShrink());
    }

    //listener for like button
    public void likeIconClicked() {
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLiked = question.isLiked();
                if (!isLiked) {
                    question.likePost(currentUser);
                } else {
                    question.unlikePost(currentUser);
                }
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

    //sets the like count on the post
    private void setLikeText(Question question, TextView view) {
        int likeCount = question.getLikeCount();
        String itemsFound = getResources().getQuantityString(R.plurals.numberOfLikes, likeCount, likeCount);
        view.setText(itemsFound);
    }

    //sets the comment count on the post
    private void setReplyText(Question question, TextView view) {
        int replyCount = question.getRepliesCount();
        String itemsFound = getResources().getQuantityString(R.plurals.numberOfReplies, replyCount, replyCount);
        view.setText(itemsFound);
    }

    public void setUpForVerifiedResponses() {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isToggleEnable = true;
                    queryOnlyVerifiedResponse();
                } else {
                    isToggleEnable = false;
                    queryReply();
                }
            }
        });
    }

    public void queryOnlyVerifiedResponse() {
        final ParseQuery<Reply> query = ParseQuery.getQuery(Reply.class);
        query.addDescendingOrder(Reply.KEY_CREATED_AT);
        query.whereEqualTo(Reply.KEY_QUESTION_ID, question);
        query.findInBackground(new FindCallback<Reply>() {
            @Override
            public void done(List<Reply> replies, ParseException e) {
                if (e != null) {
                    Toast.makeText(QuestionDetailsActivity.this, getString(R.string.TOAST_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                    return;
                }
                replyAdapter.clear();
                replyAdapter.addOnlyVerifiedResponse(replies);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}