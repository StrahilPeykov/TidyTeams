package com.example.tidyteams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<AttendeeListItem> attendeeList;

    public AttendeeListAdapter(Context context, ArrayList<AttendeeListItem> attendeeList) {
        this.context = context;
        this.attendeeList = attendeeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.attendee_list_item, parent,
                false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AttendeeListItem item = attendeeList.get(position);
        holder.username.setText(item.getUsername());
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
        }
    }
}
