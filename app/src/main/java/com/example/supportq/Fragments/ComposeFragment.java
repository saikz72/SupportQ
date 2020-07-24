package com.example.supportq.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final String TAG = "ComposeFragment";
    private Button btnCompose;
    private EditText etCompose;
    private ImageView btnCaptureImage;
    private ImageView ivMedia;
    private File photoFile;
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
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        ivMedia = view.findViewById(R.id.ivMedia);
        submitButtonClicked();
        takePhotoButtonClicked();
    }

    public void submitButtonClicked() {
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etCompose.getText().toString();
                if (validatePost(description)) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    saveQuestion(description, currentUser);
                    FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    Fragment homeFragment = new HomeFragment();
                    fragmentTransaction.replace(R.id.flContainer, homeFragment);
                    fragmentTransaction.commit();
                    Toast.makeText(getContext(), getString(R.string.QUESTION_POSTED_TOAST), Toast.LENGTH_SHORT).show();
                }
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
        question.setImage(new ParseFile(photoFile));
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(getString(R.string.PHOTO_FILENAME));
        Uri fileProvider = FileProvider.getUriForFile(getContext(), getString(R.string.AUTHORITY), photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Toast.makeText(getContext(), getString(R.string.FAILED_TO_CREATE_DIR), Toast.LENGTH_SHORT).show();
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivMedia.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), getString(R.string.PICTURE_NOT_TAKEN), Toast.LENGTH_SHORT).show();
            }
        }
    }
}