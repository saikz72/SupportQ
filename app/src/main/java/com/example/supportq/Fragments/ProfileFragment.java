package com.example.supportq.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.supportq.Activities.EditProfileActivity;
import com.example.supportq.Activities.LoginActivity;
import com.example.supportq.R;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private Button btnEditProfile;
    private Button btnLogOut;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogOut = view.findViewById(R.id.btnLogOut);

        // TODO -- redesign profile layout
        editProfileButtonClicked();
        logoutButtonClicked();


    }

    //edit profile
    public void editProfileButtonClicked() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    //logout
    public void logoutButtonClicked(){
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}