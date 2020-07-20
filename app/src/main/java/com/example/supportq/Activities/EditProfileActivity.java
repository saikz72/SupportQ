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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;


public class EditProfileActivity extends AppCompatActivity {
    public static final String TAG = "EditProfileActivity";
    public static final String PHOTO_FILENAME = "photo.jpg";
    public static final String authority = "com.codepath.fileprovider.supportq";
    public static final String FAILED_TO_CREATE_DIR = "failed to create directory";
    public static final String PICTURE_NOT_TAKEN = "No Picture was taken!";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private ParseUser currentUser;
    private Button btnSubmit;
    private Button btnCaptureImage;
    private TextInputLayout etUsername;
    private ImageView ivProfilePicture;
    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        currentUser = ParseUser.getCurrentUser();
        setViews();
        bindViews();
        takePhotoButtonClicked();
        submitButtonClicked();
    }

    public void setViews() {
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        etUsername = findViewById(R.id.etUsername);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
    }

    public void bindViews() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        etUsername.getEditText().setText(currentUser.getUsername());      //user's current name;
    }

    //capture image
    public void takePhotoButtonClicked() {
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    public void launchCamera() {
        //create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(PHOTO_FILENAME);
        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(EditProfileActivity.this, authority, photoFile);
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
            Toast.makeText(this, FAILED_TO_CREATE_DIR, Toast.LENGTH_SHORT).show();
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
                ivProfilePicture.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, PICTURE_NOT_TAKEN, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void submitButtonClicked() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                if (isUsernameChangeValid(username)) {
                    saveChanges(currentUser, photoFile, username);
                }
            }
        });
    }

    public boolean isUsernameChangeValid(String username) {
        if (!Validator.isUsernameLongEnough(username)) {
            etUsername.setError(Validator.USERNAME_LENGTH_ERROR);
            return false;
        }
        return true;
    }

    public void saveChanges(ParseUser currentUser, File pic, String username) {
        //breaks if profile picutre is not taken
        currentUser.put(User.KEY_USERNAME, username);
        if (pic == null) {
            finish();
            return;
        }
        ParseFile parseFile = new ParseFile(pic);
        currentUser.put(User.KEY_PROFILE_PICTURE, parseFile);
        currentUser.saveInBackground();
        finish();
    }
}