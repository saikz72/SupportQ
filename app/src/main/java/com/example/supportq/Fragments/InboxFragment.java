package com.example.supportq.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.supportq.Adapters.EventAdapter;
import com.example.supportq.Models.Event;
import com.example.supportq.Models.Question;
import com.example.supportq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {
    List<Event> allEvents;
    RecyclerView rvEvents;
    EventAdapter eventAdapter;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allEvents = new ArrayList<>();
        eventAdapter = new EventAdapter(allEvents, getContext());
        rvEvents = view.findViewById(R.id.rvEvents);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(linearLayout);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(), linearLayout.getOrientation());
        rvEvents.addItemDecoration(divider);
        queryEvents();
    }

    public void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_USER);
        query.addDescendingOrder(Event.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                    return;
                }
                allEvents.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }
        });
    }
}