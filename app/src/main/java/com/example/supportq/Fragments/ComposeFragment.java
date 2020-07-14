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

import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ComposeFragment extends Fragment {
    private Button btnSubmit;
    private EditText etDescription;

    public ComposeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etDescription = view.findViewById(R.id.etDescription);
        submitButtonClicked();
    }

    public void submitButtonClicked(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                //check that question is not empty
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveQuestion(description, currentUser);
            }
        });
    }

    private void saveQuestion(String description, ParseUser currentUser) {
        Question question =  new Question();
        question.setDescription(description);
        question.setUser(currentUser);
        question.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //check if save succesfully
                if(e != null){
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
                    return;
                }
                etDescription.setText("");
            }
        });
    }
}