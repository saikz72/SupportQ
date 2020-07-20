package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.supportq.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        setViews();
    }

    public void setViews(){
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
    
    public void bind(){

    }
}