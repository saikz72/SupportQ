package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class ComposeActivity extends AppCompatActivity {
    private Button btnCompose;
    private EditText etCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        btnCompose = findViewById(R.id.btnCompose);
        etCompose = findViewById(R.id.etCompose);
        submitButtonClicked();
    }

    public void submitButtonClicked() {
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etCompose.getText().toString();
                //check that question is not empty
                if (validatePost(description)) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    Question question = saveQuestion(description, currentUser);
                    Intent intent = new Intent();
                    intent.putExtra("compose", Parcels.wrap(question));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private boolean validatePost(String description) {
        if (description.isEmpty()) {
            etCompose.setError("question cannot be empty");
            etCompose.setBackgroundResource(R.drawable.error_background);
            return false;
        }
        if (description.length() < 5){
            etCompose.setError("question is too short");
            etCompose.setBackgroundResource(R.drawable.error_background);
            return false;
        }
        return true;
    }

    private Question saveQuestion(String description, ParseUser currentUser) {
        Question question = new Question();
        question.setDescription(description);
        question.setUser(currentUser);
        question.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //check if save succesfully
                if (e != null) {
                    Toast.makeText(ComposeActivity.this, "Error while saving!", Toast.LENGTH_LONG).show();
                    return;
                }
                etCompose.setText("");
            }
        });
        return question;
    }
}