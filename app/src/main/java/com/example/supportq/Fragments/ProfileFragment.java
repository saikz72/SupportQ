package com.example.supportq.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.supportq.Activities.EditProfileActivity;
import com.example.supportq.Activities.LoginActivity;
import com.example.supportq.Adapters.ProfileAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private Button btnEditProfile;
    private Button btnLogOut;
    private ProgressBar progressBar;
    private ProfileAdapter profileAdapter;
    private List<Question> allQuestions;
    private RecyclerView rvQuestion;
    ParseFile profilePicture;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViews(view);
        allQuestions = new ArrayList<>();
        profileAdapter = new ProfileAdapter(allQuestions, getContext());
        //set the adapter to the rv
        rvQuestion.setAdapter(profileAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvQuestion.setHasFixedSize(true);
        //set the layout manager on the recycler view
        rvQuestion.setLayoutManager(linearLayoutManager);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        try {
            profilePicture = ParseUser.getCurrentUser().fetch().getParseFile(("profilePicture"));
        } catch (ParseException e) {
            Log.e("TAG", " error", e);
        }
        if (profilePicture != null)
            Glide.with(getContext()).load(profilePicture.getUrl()).into(ivProfilePicture);
        editProfileButtonClicked();
        logoutButtonClicked();
        queryPost();
    }

    public void setViews(View view) {
        rvQuestion = view.findViewById(R.id.rvQuestions);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        progressBar = view.findViewById(R.id.pbLoading);
    }

    //edit profile
    public void editProfileButtonClicked() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    //logout
    public void logoutButtonClicked() {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void queryPost() {
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        query.addDescendingOrder(Question.KEY_CREATED_AT);  //TODO : update how the questions are ordered
        query.whereEqualTo(Question.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting posts", Toast.LENGTH_SHORT).show();
                    return;
                }
                profileAdapter.clear();
                profileAdapter.addAll(questions);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}