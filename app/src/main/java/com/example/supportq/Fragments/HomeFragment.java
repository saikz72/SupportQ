package com.example.supportq.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    private TextView tvTrendingMessage;
    private ParseUser currentUser;
    private Toolbar toolbar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView(view);
        toolbar = view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");
        queryPost();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPost();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    private void setUpRecyclerView(View view) {
        rvQuestion = view.findViewById(R.id.rvQuestions);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        tvTrendingMessage = view.findViewById(R.id.tvTrendingMessage);
        currentUser = ParseUser.getCurrentUser();
        allQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(allQuestions, getContext(), getActivity());
        rvQuestion.setAdapter(questionAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvQuestion.setLayoutManager(linearLayoutManager);
        rvQuestion.setHasFixedSize(true);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        rvQuestion.addItemDecoration(divider);
    }

    private void queryPost() {
        final ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.include(Question.KEY_USER);
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                questionAdapter.clear();
                for (int i = 0; i < questions.size(); i++) {
                    if (!questions.get(i).getIsDeleted() && !questions.get(i).didUserHideQuestion()) {
                        allQuestions.add(questions.get(i));
                    }
                }
                questionAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();           //TODO --> check workchat
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_like:
                likeSort();
                return true;
            case R.id.action_sort_date:
                dateSort();
                return true;
            case R.id.action_sort_trending:
                trendingSort();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void trendingSort() {
        final ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.include(Question.KEY_USER);
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                questionAdapter.clear();
                boolean arePostTrending = questionAdapter.addTrendingPost(questions);
                if (!arePostTrending) {
                    rvQuestion.setVisibility(View.GONE);
                    tvTrendingMessage.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void dateSort() {
        tvTrendingMessage.setVisibility(View.GONE);
        rvQuestion.setVisibility(View.VISIBLE);
        final ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.include(Question.KEY_USER);
        query.addDescendingOrder(Question.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                questionAdapter.clear();
                for (int i = 0; i < questions.size(); i++) {
                    if (!questions.get(i).getIsDeleted() && !questions.get(i).didUserHideQuestion()) {
                        allQuestions.add(questions.get(i));
                    }
                }
                questionAdapter.notifyDataSetChanged();
            }
        });
    }

    private void likeSort() {
        tvTrendingMessage.setVisibility(View.GONE);
        rvQuestion.setVisibility(View.VISIBLE);
        rvQuestion.setVisibility(View.VISIBLE);
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.include(Question.KEY_USER);
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                questionAdapter.clear();
                questionAdapter.addAll(questions);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        queryPost();
    }
}