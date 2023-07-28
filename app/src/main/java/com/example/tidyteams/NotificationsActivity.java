package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsActivity extends BaseActivity implements RecyclerViewEventInterface {
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, EventsRef;

    private String join;

    private RecyclerView eventList;

    private ArrayList<Events> list;

    private DatabaseReference eventRef, attendRef;
    private String currentUserId;


    private NotificationsAdapter adapter;


    //method that populates recycler view notifications_recycler_view
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupTopMenu();
        setSelectedIcon(notificationsButton);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        attendRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Attendees");

        UsersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users");
        EventsRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Posts");

        eventList = findViewById(R.id.notifications_recycler_view);
        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new NotificationsAdapter(this, list, this);
        eventList.setAdapter(adapter);

        EventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                    attendRef.child(dataSnapshot1.getKey()).child(currentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.exists()) {
                                Events events = dataSnapshot1.getValue(Events.class);

                                if (snapshot2.getValue().toString().equals("true")) {
                                    list.add(events);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }

    @Override
    public void onEventClick(int position) {
        Intent intent = new Intent(this, EventMainPageActivity.class);
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

    @Override
    public void onAttendeesButtonClick(int position) {

    }
}
