package com.example.supportq.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.supportq.Activities.MainActivity;
import com.example.supportq.Adapters.QuestionAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvQuestion;
    private QuestionAdapter questionAdapter;
    private List<Question> allQuestions;
    private SwipeRefreshLayout swipeContainer;
    private ParseUser currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvQuestion = view.findViewById(R.id.rvQuestions);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        currentUser = ParseUser.getCurrentUser();
        allQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(allQuestions, getContext(), getActivity());
        //set the adapter to the rv
        rvQuestion.setAdapter(questionAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //set the layout manager on the recycler view
        rvQuestion.setLayoutManager(linearLayoutManager);
        rvQuestion.setHasFixedSize(true);
        queryPost();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPost();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        rvQuestion.addItemDecoration(divider);
    }

    private void queryPost() {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.include(Question.KEY_USER);
        query.addDescendingOrder(Question.KEY_CREATED_AT);  //TODO : update how the questions are ordered
        query.setLimit(MainActivity.MAX_NUMBER_POST); //limit the return post
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                questionAdapter.clear();
                questionAdapter.addAll(questions);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}