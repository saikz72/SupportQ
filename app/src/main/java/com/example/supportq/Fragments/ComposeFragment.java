package com.example.supportq.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ComposeFragment extends Fragment {
    private Button btnCompose;
    private EditText etCompose;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCompose = view.findViewById(R.id.btnCompose);
        etCompose = view.findViewById(R.id.etCompose);
        submitButtonClicked();
    }

    public void submitButtonClicked() {
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressIndicator.showMessage(getContext());
                String description = etCompose.getText().toString();
                //check that question is not empty
                if (validatePost(description)) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    saveQuestion(description, currentUser);
                    ProgressIndicator.hideMessage(getContext());
                    Toast.makeText(getContext(), getString(R.string.QUESTION_POSTED_TOAST), Toast.LENGTH_SHORT).show();
                }
                ProgressIndicator.hideMessage(getContext());
            }
        });
    }

    private boolean validatePost(String description) {
        if (description.isEmpty()) {
            etCompose.setError(getString(R.string.QUESTION_EMPTY_ERROR));
            etCompose.setBackgroundResource(R.drawable.error_background);
            return false;
        }
        if (description.length() < 5) {
            etCompose.setError(getString(R.string.QUESTION_LENGTH_ERROR));
            etCompose.setBackgroundResource(R.drawable.error_background);
            return false;
        }
        return true;
    }

    private void saveQuestion(String description, ParseUser currentUser) {
        Question question = new Question();
        question.setDescription(description);
        question.setUser(currentUser);
        question.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //check if save succesfully
                if (e != null) {
                    Toast.makeText(getContext(), getString(R.string.SAVING_ERROR), Toast.LENGTH_LONG).show();
                    return;
                }
                etCompose.setText("");
            }
        });
    }
}