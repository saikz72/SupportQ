package com.example.supportq.Activities.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supportq.R;

public class EditQuestionActivity extends AppCompatActivity {
    private EditText etEditQuestion;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        etEditQuestion = findViewById(R.id.etEditQuestion);
        btnSave = findViewById(R.id.btnSave);
        String description = getIntent().getStringExtra(getString(R.string.edit_item_key));
        etEditQuestion.setText(description);
        saveQuestion();
    }

    private void saveQuestion() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEditQuestion.getText().toString().equals("")) {
                    Toast.makeText(EditQuestionActivity.this, getString(R.string.question_edit_message), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.edit_item_key), etEditQuestion.getText().toString());
                intent.putExtra(getString(R.string.edit_item_position), getIntent().getExtras().getInt(getString(R.string.edit_item_position)));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}