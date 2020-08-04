package com.example.supportq.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Activities.EditProfileActivity;
import com.example.supportq.Activities.LoginActivity;

import com.example.supportq.Adapters.SectionPagerAdapter;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private Button btnEditProfile;
    private Button btnLogOut;
    private TextView tvFullname;
    private ParseUser currentUser;
    private ParseFile profilePicture;
    private View myFragment;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static final int EDIT_CODE = 1;
    public static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragment =  inflater.inflate(R.layout.fragment_profile, container, false);
        viewPager = myFragment.findViewById(R.id.viewPager);
        tabLayout = myFragment.findViewById(R.id.tabLayout);
        return myFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_CODE && resultCode == RESULT_OK){
            String username = data.getStringExtra(getString(R.string.CODE));
            String fullName = data.getStringExtra(getString(R.string.CODE_FULLNAME));
            tvUsername.setText("@" + username);
            tvFullname.setText(fullName);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new UserPostFragment(), getString(R.string.user_post));
        adapter.addFragment(new BookMarkFragment(), getString(R.string.bookmark));
        adapter.addFragment(new HiddenPostFragment(), getString(R.string.hidden));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = ParseUser.getCurrentUser();
        setViews(view);
        bindViews();
        setUpEditProfileButtonListener();
        setUpLogOutButtonListener();
    }

    private void bindViews() {
        tvUsername.setText("@" + User.getUserName(currentUser));        //TODO--> use string resource(and concat)
        tvFullname.setText(User.getFullName(currentUser));
        try {
            profilePicture = ParseUser.getCurrentUser().fetch().getParseFile((User.KEY_PROFILE_PICTURE));
        } catch (ParseException e) {
            // no internet, user null .....
            Log.e("TAG", " error", e);
        }
        if (profilePicture != null) {
            Glide.with(getContext()).load(profilePicture.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
        }else{
            ivProfilePicture.setImageResource(R.drawable.profile_image_default);
        }
    }

    public void setViews(View view) {
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        tvFullname = view.findViewById(R.id.tvFullname);
    }

    //edit profile
    public void setUpEditProfileButtonListener() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivityForResult(intent, EDIT_CODE);
            }
        });
    }

    //logout
    public void setUpLogOutButtonListener() {
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