package com.example.tidyteams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {

    private final RecyclerViewEventInterface recyclerViewEventInterface;

    public NotificationsAdapter(Context context, ArrayList<Events> list,
                                NotificationsActivity recyclerViewEventInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewEventInterface = recyclerViewEventInterface;
    }

    Context context;
    ArrayList<Events> list;

    @NonNull
    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.event_card_template, parent, false);
        return new MyViewHolder(v, recyclerViewEventInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Events events = list.get(position);
        holder.eventTime.setText(events.getTime());
        holder.eventTitle.setText(events.getTitle());
        holder.eventDate.setText(events.getDate());
        String location =
                events.getCountry() + ", " + events.getRegion() + ", " + events.getPostcode() +
                        ", " + events.getStreet() + ", " + events.getNumber();
        holder.eventLocation.setText(location);

        Glide.with(context)
                .load(events.getPostimage())
                .into(holder.eventImageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public String UID;
        TextView eventTitle, eventDate, eventTime, eventLocation, eventCreator;
        ImageView eventImageView;

        public MyViewHolder(@NonNull View itemView,
                            RecyclerViewEventInterface recyclerViewEventInterface) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.event_title);
            eventDate = itemView.findViewById(R.id.event_date);
            eventTime = itemView.findViewById(R.id.event_time);
            eventLocation = itemView.findViewById(R.id.event_place);
            eventImageView = itemView.findViewById(R.id.event_image_view);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewEventInterface != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewEventInterface.onEventClick(position);
                        }
                    }
                }
            });

        }
    }
}
