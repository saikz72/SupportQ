package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class ComposeActivity extends AppCompatActivity {
    private Button btnCompose;
    private TextView tvComposeCount;
    private EditText etCompose;
    public static final int MAX_QUESTION_LENGTH = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        btnCompose = findViewById(R.id.btnCompose);
        tvComposeCount = findViewById(R.id.tvComposeCount);
        etCompose = findViewById(R.id.etCompose);

        submitButtonClicked();
        textCountListener();
    }

    public void textCountListener(){
        //listener to display the character count left as user types in new tweet
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = etCompose.getText().toString();
                int textCount = MAX_QUESTION_LENGTH - text.length();
                tvComposeCount.setText(textCount + "/" + MAX_QUESTION_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void submitButtonClicked(){
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etCompose.getText().toString();
                //check that question is not empty
                if (description.isEmpty()) {
                    Toast.makeText(ComposeActivity.this,  "Description cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                Question question = saveQuestion(description, currentUser);
                Intent intent = new Intent();
                intent.putExtra("compose", Parcels.wrap(question));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private Question saveQuestion(String description, ParseUser currentUser) {
        Question question =  new Question();
        question.setDescription(description);
        question.setUser(currentUser);
        question.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //check if save succesfully
                if(e != null){
                    Toast.makeText(ComposeActivity.this, "Error while saving!", Toast.LENGTH_LONG).show();
                    return;
                }
                etCompose.setText("");
            }
        });
        return question;
    }
}