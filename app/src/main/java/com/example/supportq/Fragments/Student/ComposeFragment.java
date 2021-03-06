package com.example.supportq.Fragments.Student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.supportq.Activities.Student.MainActivity;
import com.example.supportq.Fragments.HomeFragment;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final String TAG = "ComposeFragment";
    private EditText etCompose;
    private ImageView ivMedia;
    private File photoFile;
    private Toolbar toolbar;
    private ProgressBar pbLoading;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");
    }

    public void setViews(View view){
        etCompose = view.findViewById(R.id.etCompose);
        ivMedia = view.findViewById(R.id.ivMedia);
        toolbar = view.findViewById(R.id.toolbar);
        pbLoading = view.findViewById(R.id.pbLoading);
    }

    @Override
    public void onResume() {
        super.onResume();
        etCompose.post(new Runnable() {
            @Override
            public void run() {
                etCompose.requestFocus();
                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.showSoftInput(etCompose, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public void setUpSubmitButtonListener() {
        String description = etCompose.getText().toString();
        if (validatePost(description)) {
            pbLoading.setVisibility(View.VISIBLE);
            ParseUser currentUser = ParseUser.getCurrentUser();
            saveQuestion(description, currentUser);
            FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
            Fragment homeFragment = new HomeFragment();
            fragmentTransaction.replace(R.id.flContainer, homeFragment);
            fragmentTransaction.commit();
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);      //move selected icon in bottom nav to home
            final Snackbar snackbar = Snackbar.make(MainActivity.snackbar, R.string.home_snackbar, Snackbar.LENGTH_SHORT);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            }).setActionTextColor(getResources().getColor(R.color.green)).setDuration(3000).show();
        }
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
        if (photoFile != null) {
            question.setImage(new ParseFile(photoFile));
        }
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
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                ivMedia.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), getString(R.string.PICTURE_NOT_TAKEN), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.compose_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_take_picture:
                launchCamera();
                return true;
            case R.id.action_submit:
                setUpSubmitButtonListener();
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }
}