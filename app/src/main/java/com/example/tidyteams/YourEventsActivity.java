package com.example.tidyteams;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class YourEventsActivity extends BaseActivity implements RecyclerViewEventInterface {
    private FirebaseAuth mAuth;
    private DatabaseReference EventsRef;

    private RecyclerView yourEventsList;

    private ArrayList<Events> list;

    private DatabaseReference eventRef, createRef;

    private MyAdapterAdmin adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_events);

        mAuth = FirebaseAuth.getInstance();
        EventsRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Posts");
        createRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Attendees");

        yourEventsList = findViewById(R.id.your_events_recycler_view);
        yourEventsList.setHasFixedSize(true);
        yourEventsList.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new MyAdapterAdmin(this, list, this);
        yourEventsList.setAdapter(adapter);

        String currentUserId = mAuth.getCurrentUser().getUid();
        Query query = EventsRef.orderByChild("uid").equalTo(currentUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Events event = snapshot.getValue(Events.class);
                    list.add(event);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onEventClick(int position) {
        Events clickedEvent = list.get(position);
        Intent eventDetailsIntent = new Intent(YourEventsActivity.this, EditEventActivity.class); // Changed to EditEventActivity
        eventDetailsIntent.putExtra("event_id", clickedEvent.getPostID());
        eventDetailsIntent.putExtra("is_create_mode", false);
        startActivity(eventDetailsIntent);
    }

    @Override
    public void onAttendeesButtonClick(int position) {
        Intent intent = new Intent(this, AttendeeListActivity.class);
        intent.putExtra("Title", list.get(position).getTitle());
        intent.putExtra("Date", list.get(position).getDate());
        intent.putExtra("Time", list.get(position).getTime());
        String location =
                list.get(position).getCountry() + ", " + list.get(position).getRegion() + ", " + list.get(position).getPostcode() + ", " + list.get(position).getStreet() + ", " + list.get(position).getNumber();
        intent.putExtra("Location", location);
        intent.putExtra("Description", list.get(position).getDescription());
        intent.putExtra("Image", list.get(position).getPostimage());
        intent.putExtra("UID", list.get(position).getUid());
        intent.putExtra("PostID", list.get(position).getPostID());
        startActivity(intent);
    }


}
