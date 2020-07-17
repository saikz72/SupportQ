package com.example.supportq.Fragments;

import android.app.Activity;
import android.content.Intent;
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

import com.example.supportq.Activities.ComposeActivity;
import com.example.supportq.Adapters.QuestionAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvQuestion;
    private QuestionAdapter questionAdapter;
    private List<Question> allQuestions;
    private FloatingActionButton floatingActionButton;
    public final int REQUEST_CODE = 10;

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
        rvQuestion = view.findViewById(R.id.rvQuestions);
        floatingActionButton = view.findViewById(R.id.floating_action_button);
        allQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(allQuestions, getContext());
        //set the adapter to the rv
        rvQuestion.setAdapter(questionAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //set the layout manager on the recycler view
        rvQuestion.setLayoutManager(linearLayoutManager);
        floatingActionButtonClicked();
        queryPost();
    }

    public void floatingActionButtonClicked(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Question question = Parcels.unwrap(data.getParcelableExtra("compose"));
            allQuestions.add( 0, question);
            questionAdapter.notifyItemInserted(0);
            rvQuestion.scrollToPosition(0);
        }
    }

    private void queryPost() {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.addDescendingOrder(Question.KEY_CREATED_AT);  //TODO : update how the questions are ordered
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