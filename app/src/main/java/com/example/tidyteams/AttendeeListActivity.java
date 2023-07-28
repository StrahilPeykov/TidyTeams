package com.example.tidyteams;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AttendeeListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference eventsRef, attendeesRef, usersRef;

    private ArrayList<String> usernames;
    private ArrayList<AttendeeListItem> attendeeListItems;

    private AttendeeListAdapter adapter;

    private RecyclerView attendeesRecyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);

        initializeFirebaseReferences();
        setupRecyclerView();
        loadEventData();
        loadAttendees();
    }

    private void initializeFirebaseReferences() {
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Posts");
        attendeesRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Attendees");
        usersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users");
    }

    private void setupRecyclerView() {
        attendeesRecyclerView = findViewById(R.id.attendee_list);
        attendeeListItems = new ArrayList<>();
        usernames = new ArrayList<>();
        adapter = new AttendeeListAdapter(this, attendeeListItems);
        attendeesRecyclerView.setAdapter(adapter);
        attendeesRecyclerView.setHasFixedSize(true);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void loadEventData() {
        // Get the event data from the intent if necessary
    }

    private void loadAttendees() {
        String postID = getIntent().getStringExtra("PostID");

        attendeesRef.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot attendeeSnapshot : dataSnapshot.getChildren()) {
                        String attendeeId = attendeeSnapshot.getKey();
                        addUserToList(attendeeId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addUserToList(String attendeeId) {
        usersRef.child(attendeeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    addAttendeeListItem(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addAttendeeListItem(String username) {
        AttendeeListItem item = new AttendeeListItem(username);
        attendeeListItems.add(item);
        adapter.notifyDataSetChanged();
    }
}
