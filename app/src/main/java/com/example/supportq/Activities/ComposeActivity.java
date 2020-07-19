package com.example.supportq.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

public class ComposeActivity extends AppCompatActivity {
    private static final String TAG = "ComposeActivity" ;
    public File photoFile;
    public static final String photoFileName = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Button btnCompose;
    private EditText etCompose;
    private ImageView ivMedia;
    private ImageView ivCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        btnCompose = findViewById(R.id.btnCompose);
        etCompose = findViewById(R.id.etCompose);
        ivMedia = findViewById(R.id.ivMedia);
        ivCamera = findViewById(R.id.ivCamera);
        submitButtonClicked();
        cameraButtonClicked();
    }

    public void submitButtonClicked() {
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etCompose.getText().toString();
                //check that question is not empty
                if (validatePost(description)) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    Question question = saveQuestion(description, currentUser, photoFile);
                    Intent intent = new Intent();
                    intent.putExtra("compose", Parcels.wrap(question));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void cameraButtonClicked(){
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
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

    private Question saveQuestion(String description, ParseUser currentUser, File photoFile) {
        Question question = new Question();
        question.setDescription(description);
        question.setUser(currentUser);
        question.setImage(new ParseFile(photoFile));
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
    private void launchCamera() {
        //create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);
        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileprovider.supportq", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Toast.makeText(this, "failed to create directory", Toast.LENGTH_SHORT).show();
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivMedia.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "No Picture was taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}