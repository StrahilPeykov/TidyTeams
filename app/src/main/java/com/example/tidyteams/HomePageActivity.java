package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageActivity extends BaseActivity implements RecyclerViewEventInterface {

    private FirebaseAuth mAuth;
    private DatabaseReference EventsRef;

    private RecyclerView eventList;

    private ArrayList<Events> list;

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        EventsRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Posts");

        eventList = findViewById(R.id.all_events_recycler_view);
        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        list = new ArrayList<>();
        adapter = new MyAdapter(this, list, this);
        eventList.setAdapter(adapter);

        EventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Events events = dataSnapshot.getValue(Events.class);
                    events.setPostimage(dataSnapshot.child("postimage").getValue().toString());
                    list.add(events);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setupTopMenu();
        setSelectedIcon(homeButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(HomePageActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onEventClick(int position) {
        Intent intent = new Intent(this, EventMainPageActivity.class);
        intent.putExtra("Title", list.get(position).getTitle());
        intent.putExtra("Date", list.get(position).getDate());
        intent.putExtra("Time", list.get(position).getTime());
        String location = list.get(position).getCountry() + ", " + list.get(position).getRegion() + ", " + list.get(position).getPostcode() + ", "+ list.get(position).getStreet() + ", " + list.get(position).getNumber();
        intent.putExtra("Location", location);
        intent.putExtra("Description", list.get(position).getDescription());
        intent.putExtra("Image", list.get(position).getPostimage());
        intent.putExtra("UID", list.get(position).getUid());
        intent.putExtra("PostID", list.get(position).getPostID());
        //intent.putExtra("attendeeLimit", list.get(position).getAttendeeLimit());
        startActivity(intent);
    }

    @Override
    public void onAttendeesButtonClick(int position) {

    }
}
