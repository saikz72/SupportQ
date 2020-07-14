package com.example.supportq.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supportq.Models.Question;
import com.example.supportq.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<Question> allQuestions;
    private Context context;

    public QuestionAdapter(List<Question> allQuestions, Context context) {
        this.allQuestions = allQuestions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = allQuestions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return allQuestions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            //tvDescription.setVisibility(View.GONE);
        }

        public void bind(Question question) {
            tvDescription.setText(question.getDescription());
        }
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
}
