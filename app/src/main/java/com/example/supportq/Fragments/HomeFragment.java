package com.example.supportq.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.supportq.Adapters.PostAdapter;
import com.example.supportq.Models.Post;
import com.example.supportq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvPost;
    private PostAdapter postAdapter;
    private List<Post> allPost;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPost = view.findViewById(R.id.rvPost);

        allPost = new ArrayList<>();
        postAdapter = new PostAdapter(allPost, getContext());

        //set the adapter to the rv
        rvPost.setAdapter(postAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //set the layout manager on the recycler view
        rvPost.setLayoutManager(linearLayoutManager);
        queryPost();
    }

    private void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue with getting posts", Toast.LENGTH_SHORT).show();
                    return;
                }
                postAdapter.addAll(posts);
            }
        });
    }
}