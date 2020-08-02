package com.example.supportq.Fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.supportq.Activities.Student.EditQuestionActivity;
import com.example.supportq.Adapters.ProfileAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;

public class UserPostFragment extends Fragment {
    private ProfileAdapter profileAdapter;
    private List<Question> allQuestions;
    private RecyclerView rvQuestion;
    private ParseUser currentUser;
    private ProgressBar progressBar;
    public static final int EDIT_TEXT_CODE = 20;


    public UserPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_post, container, false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(getString(R.string.edit_item_key));
            int position = data.getExtras().getInt((getString(R.string.edit_item_position)));
            Question question = allQuestions.get(position);
            question.setDescription(itemText);
            profileAdapter.notifyDataSetChanged();
            question.saveInBackground();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
        ProfileAdapter.OnEditIconClicked onEditIconClicked = new ProfileAdapter.OnEditIconClicked() {
            @Override
            public void onQuestionClicked(int position) {
                Intent intent = new Intent(getContext(), EditQuestionActivity.class);
                Question question = allQuestions.get(position);
                intent.putExtra(getString(R.string.edit_item_key), question.getDescription());
                intent.putExtra(getString(R.string.edit_item_position), position);
                startActivityForResult(intent, EDIT_TEXT_CODE);
            }
        };
        profileAdapter = new ProfileAdapter(allQuestions, getContext(), onEditIconClicked);
        rvQuestion.setAdapter(profileAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvQuestion.setHasFixedSize(true);
        rvQuestion.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        rvQuestion.addItemDecoration(divider);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvQuestion);
        queryPost();
    }

    public void setViews(View view) {
        rvQuestion = view.findViewById(R.id.rvQuestions);
        progressBar = view.findViewById(R.id.pbLoading);
        allQuestions = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            final Question question = allQuestions.get(position);
            View.OnClickListener myOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question.setIsDeleted(false);
                    question.saveInBackground();
                    allQuestions.add(position, question);
                    profileAdapter.notifyItemInserted(position);
                }
            };
            Snackbar.make(rvQuestion, R.string.snackbar_text, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.snackbar_action, myOnClickListener)
                    .setActionTextColor(getResources().getColor(R.color.green))
                    .setDuration(3000).show();
            question.setIsDeleted(true);
            question.saveInBackground();
            allQuestions.remove(position);
            profileAdapter.notifyItemRemoved(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.floating_buttor_color))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void queryPost() {
        progressBar.setVisibility(View.VISIBLE);
        final ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.addDescendingOrder(Question.KEY_CREATED_AT);
        query.whereEqualTo(Question.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < questions.size(); i++) {
                    if (!questions.get(i).getIsDeleted()) {
                        allQuestions.add(questions.get(i));
                    }
                }
                profileAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}