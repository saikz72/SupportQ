package com.example.supportq.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.supportq.Models.Event;
import com.example.supportq.R;
import com.parse.ParseFile;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<Event> allEvents;
    private Context context;

    public EventAdapter(List<Event> events, Context context) {
        this.allEvents = events;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mail, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvUsername;
        private TextView tvBody;
        private TextView tvStartTime;
        private TextView tvEndTime;
        private ImageView ivProfilePicture;
        private TextView tvTimeStamp;
        private ImageView ivEventIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ivEventIcon = itemView.findViewById(R.id.ivEventIcon);
        }

        public void bind(int position) {
            Event event = allEvents.get(position);
            String eventType = event.getEvent();
            String username = event.getUser().getUsername();
            tvUsername.setText(username);
            tvStartTime.setText(event.getStartTime());
            tvEndTime.setText(event.getEndTime());
            tvDate.setText(event.getStartDate());
            ParseFile parseFile = event.getImage();
            if (parseFile != null) {
                Glide.with(context).load(parseFile.getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
            } else {
                ivProfilePicture.setImageResource(R.drawable.profile_image_default);
            }
            if (eventType.equals(context.getString(R.string.workshop))) {
                ivEventIcon.setImageResource(R.drawable.workshop_icon);
                tvBody.setText(username + " " + context.getString(R.string.hosting_workshop) + " " + eventType);
            } else {
                ivEventIcon.setImageResource(R.drawable.office_hour_icon);
                tvBody.setText(username + " " + context.getString(R.string.hosting_office_hour) + " " + eventType);
            }
            tvTimeStamp.setText(event.getCreatedTimeAgo());
        }
    }
}
