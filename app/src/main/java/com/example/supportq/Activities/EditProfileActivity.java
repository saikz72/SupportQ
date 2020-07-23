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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;


public class EditProfileActivity extends AppCompatActivity {
    public static final String TAG = "EditProfileActivity";
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
        try {
            bindViews();
        } catch (ParseException e) {
        }
        takePhotoButtonClicked();
        submitButtonClicked();
    }

    public void setViews() {
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        etUsername = findViewById(R.id.etUsername);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
    }

    public void bindViews() throws ParseException {
        ParseUser currentUser = ParseUser.getCurrentUser();
        etUsername.getEditText().setText(currentUser.getUsername());      //user's current name;
        ParseFile profilePicture = ParseUser.getCurrentUser().fetch().getParseFile((User.KEY_PROFILE_PICTURE));
        if (profilePicture != null)
            Glide.with(this).load(profilePicture.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);   //user's current profile picture
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
        photoFile = getPhotoFileUri(getString(R.string.PHOTO_FILENAME));
        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(EditProfileActivity.this, getString(R.string.AUTHORITY), photoFile);
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
            Toast.makeText(this, getString(R.string.FAILED_TO_CREATE_DIR), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, getString(R.string.PICTURE_NOT_TAKEN), Toast.LENGTH_SHORT).show();
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
            etUsername.setError(String.valueOf(R.string.USERNAME_LENGTH_ERROR));
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