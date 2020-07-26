package com.example.supportq.Models;

import java.util.List;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class TextViewAnimationOnClick {

    public static void onQuestionClickAnimation(ExpandableTextView tvDescription, final List<Question> allQuestions, final int position) {
        tvDescription.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean isShrink) {
                Question question = allQuestions.get(position);
                question.setShrink(isShrink);
                allQuestions.set(position, question);
            }
        });
    }

    public static void onReplyClickAnimation(ExpandableTextView tvDescription, final List<Reply> allReplies, final int position) {
        tvDescription.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean isShrink) {
                Reply reply = allReplies.get(position);
                reply.setShrink(isShrink);
                allReplies.set(position, reply);
            }
        });
    }

    public static void onQuestionClickedInDetailScreen(ExpandableTextView tvDescription, final Question question){
        tvDescription.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean isShrink) {
                question.setShrink(isShrink);
            }
        });
    }
}
