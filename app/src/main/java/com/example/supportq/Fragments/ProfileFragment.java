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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Activities.EditProfileActivity;
import com.example.supportq.Activities.EditQuestionActivity;
import com.example.supportq.Activities.LoginActivity;

import com.example.supportq.Adapters.ProfileAdapter;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private Button btnEditProfile;
    private Button btnLogOut;
    private ProgressBar progressBar;
    private TextView tvFullname;
    private ProfileAdapter profileAdapter;
    private List<Question> allQuestions;
    private RecyclerView rvQuestion;
    private ParseFile profilePicture;
    private ParseUser currentUser;
    public static final int EDIT_TEXT_CODE = 20;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
        allQuestions = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();
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
        bindViews();
        profileAdapter = new ProfileAdapter(allQuestions, getContext(), onEditIconClicked);
        rvQuestion.setAdapter(profileAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvQuestion.setHasFixedSize(true);
        rvQuestion.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        rvQuestion.addItemDecoration(divider);
        editProfileButtonClicked();
        logoutButtonClicked();
        queryPost();
    }

    private void bindViews() {
        tvUsername.setText("@" + User.getUserName(currentUser));
        tvFullname.setText(User.getFullName(currentUser));
        try {
            profilePicture = ParseUser.getCurrentUser().fetch().getParseFile((User.KEY_PROFILE_PICTURE));
        } catch (ParseException e) {
            Log.e("TAG", " error", e);
        }
        if (profilePicture != null) {
            Glide.with(getContext()).load(profilePicture.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvQuestion);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //retrieve the updated text value
            String itemText = data.getStringExtra(getString(R.string.edit_item_key));
            //extract the original position of the edited item from the position key
            int position = data.getExtras().getInt((getString(R.string.edit_item_position)));
            //update the model at the right position with new item text
            Question question = allQuestions.get(position);
            question.setDescription(itemText);
            //notify the adapter
            profileAdapter.notifyDataSetChanged();
            question.saveInBackground();
        }
    }

    public void setViews(View view) {
        rvQuestion = view.findViewById(R.id.rvQuestions);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        progressBar = view.findViewById(R.id.pbLoading);
        tvFullname = view.findViewById(R.id.tvFullname);
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
                    Toast.makeText(getContext(), getString(R.string.FETCHING_POST_ERROR), Toast.LENGTH_SHORT).show();
                    return;
                }
                profileAdapter.clear();
                profileAdapter.addAll(questions);
                progressBar.setVisibility(View.GONE);
            }
        });
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
            Snackbar.make(rvQuestion, R.string.snackbar_text, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action, myOnClickListener).show();
            Snackbar.make(rvQuestion, R.string.snackbar_text, Snackbar.LENGTH_LONG)
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
}