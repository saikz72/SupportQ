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
import android.widget.Toast;

import com.example.supportq.Adapters.QuestionAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvPost;
    private QuestionAdapter questionAdapter;
    private List<Question> allQuestions;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPost = view.findViewById(R.id.rvPost);

        allQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(allQuestions, getContext());

        //set the adapter to the rv
        rvPost.setAdapter(questionAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //set the layout manager on the recycler view
        rvPost.setLayoutManager(linearLayoutManager);
        queryPost();
    }

    private void queryPost() {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.addDescendingOrder(Question.KEY_CREATED_AT);  //TODO : update how the questions are ordered
        query.include(Question.KEY_USER);
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting posts", Toast.LENGTH_SHORT).show();
                    return;
                }
                questionAdapter.clear();
                questionAdapter.addAll(questions);
            }
        });
    }
}