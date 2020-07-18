package com.example.supportq.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private List<Question> allQuestions;
    private Context context;
    public static final String TAG = "ProfileAdapter";

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
                setButton(holder.ivLike, !isLiked,
                        R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.likedRed);
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
        } catch (ParseException e) {
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

    private void setLikeText(Question post, TextView view) {
        int likeCount = post.getLikeCount();
        if (likeCount == 1) view.setText(String.format("%d", post.getLikeCount()));
        else view.setText(String.format("%d", post.getLikeCount()));
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

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        private ImageView ivLike;
        private TextView tvTimeStamp;
        private TextView tvLikeCount;
        private ImageView ivReply;
        private TextView tvUsername;
        private ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivReply = itemView.findViewById(R.id.ivReply);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }

        public void bind(Question question) throws ParseException {
            String username = question.getUser().fetch().getUsername();
            tvDescription.setText(question.getDescription());
            deleteButtonClicked(question);
            String timeAgo = question.getCreatedTimeAgo();
            tvTimeStamp.setText(timeAgo);
            tvUsername.setText(username);
            setButton(ivLike, question.isLiked(),
                    R.drawable.ic_vector_heart_stroke, R.drawable.ic_vector_heart, R.color.likedRed);
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

        //listener for delete button
        public void deleteButtonClicked(final Question question) {
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    try {
                        removeAt(question, position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
