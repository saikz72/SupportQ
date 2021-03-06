package com.example.supportq.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supportq.Adapters.QuestionAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HiddenPostFragment extends Fragment {
    private List<Question> allQuestions;
    private QuestionAdapter questionAdapter;
    private RecyclerView rvQuestions;
    private ProgressBar pbLoading;
    private TextView tvHidden;

    public HiddenPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hidden_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
        allQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(allQuestions, getContext(), getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvQuestions.setLayoutManager(linearLayoutManager);
        rvQuestions.setAdapter(questionAdapter);
        rvQuestions.setHasFixedSize(true);
        queryQuestions();
        if(allQuestions.size() == 0){
            tvHidden.setVisibility(View.VISIBLE);
        }else{
            tvHidden.setVisibility(View.GONE);
        }
    }

    public void setUpViews(View view) {
        rvQuestions = view.findViewById(R.id.rvQuestions);
        pbLoading = view.findViewById(R.id.pbLoading);
        tvHidden = view.findViewById(R.id.tvHidden);
    }

    public void queryQuestions() {
        pbLoading.setVisibility(View.VISIBLE);
        final ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.addDescendingOrder(Question.KEY_CREATED_AT);
        query.include(Question.KEY_USER);
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                questionAdapter.clear();
                questionAdapter.addHiddenPostByUser(questions);
                pbLoading.setVisibility(View.GONE);
            }
        });
    }
}
