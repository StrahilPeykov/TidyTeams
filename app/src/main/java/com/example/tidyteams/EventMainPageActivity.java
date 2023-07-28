package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventMainPageActivity extends AppCompatActivity {

    String role;

    FirebaseAuth mAuth;
    String currentUserId;
    int count_attendees;
    Button join;
    private DatabaseReference eventRef, attendRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_page);

        String eventTitle = getIntent().getStringExtra("Title");
        String eventDate = getIntent().getStringExtra("Date");
        String eventTime = getIntent().getStringExtra("Time");
        String eventLocation = getIntent().getStringExtra("Location");
        String eventDescription = getIntent().getStringExtra("Description");
        String eventImage = getIntent().getStringExtra("Image");
        String evntUid = getIntent().getStringExtra("UID");
        String postID = getIntent().getStringExtra("PostID");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int AttendanceLimit = extras.getInt("attendeeLimit");

        TextView eventTitleTextView = findViewById(R.id.eventMainTitle);
        TextView eventDateTextView = findViewById(R.id.eventMainDate);
        TextView eventTimeTextView = findViewById(R.id.eventMainTime);
        TextView eventLocationTextView = findViewById(R.id.eventMainPlace);
        TextView eventDescriptionTextView = findViewById(R.id.eventMainDescription);
        ImageView eventImageView = findViewById(R.id.eventMainImage);
        TextView eventCreatorImageView = findViewById(R.id.eventMainCreator);
        TextView eventAttendees = findViewById(R.id.count);
        TextView eventAttendanceLimit = findViewById(R.id.attendanceLimit);


        mAuth = FirebaseAuth.getInstance();
        attendRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Attendees").child(postID);
        eventRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Posts");
        currentUserId = mAuth.getCurrentUser().getUid();
        join = findViewById(R.id.joinButton);

        // See the UserRecord reference doc for the contents of userRecord.


        eventTitleTextView.setText(eventTitle);
        eventDateTextView.setText(eventDate);
        eventTimeTextView.setText(eventTime);
        eventLocationTextView.setText(eventLocation);
        eventDescriptionTextView.setText(eventDescription);
        eventCreatorImageView.setText(evntUid);

        //String value of AttendanceLimit
        String AttendanceLimitString = Integer.toString(AttendanceLimit);
        eventAttendanceLimit.setText(AttendanceLimitString);
        Glide.with(this)
                .load("//tidyteams-dbl15.appspot.com/Post " +
                        "Images/image:100000003329-March-202315:30.jpg")
                .into(eventImageView);
        //create a toast to show event image
        ImageView cancelButton = findViewById(R.id.cancelButton);

        // when the image is clicked, go back to the previous activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUserRole(postID);
            }


        });

        //method that counts the children that attendRef has
        //create an array list of attendees

        final int[] count = {0};
        attendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                ArrayList<String> list = new ArrayList<>();
                if (snapshot2.exists()) {
                    for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                        String attendee = snapshot3.getValue().toString();
                        if (attendee.equals("true")) {
                            list.add(attendee);
                        }
                    }
                }
                //String vlaue of list.size
                count[0] = list.size();
                String StringValueOf = Integer.toString(list.size());
                eventAttendees.setText(StringValueOf);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //dont show join button if the integer value of what is in the text view eventattendees
        // is equal to the attendance limit intger value


        if (count[0] >= AttendanceLimit) {
            join.setVisibility(View.GONE);
        }


    }

    private void toggleUserRole(String postID) {
        String newRole = "join".equalsIgnoreCase(role) ? "false" : "true";
        attendRef.child(postID).child(currentUserId).setValue(newRole)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        role = newRole;
                        updateToggleRoleButtonText();
                        Toast.makeText(EventMainPageActivity.this, "Your role has been changed!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventMainPageActivity.this,
                                "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateToggleRoleButtonText() {
        if (join.getText() == "Leave") {
            attendRef.child(currentUserId).setValue("false");
            join.setText("Join");
            Toast.makeText(EventMainPageActivity.this, "You have left the event",
                    Toast.LENGTH_SHORT).show();
        } else {
            attendRef.child(currentUserId).setValue("true");
            join.setText("Leave");
            Toast.makeText(EventMainPageActivity.this, "You have joined the event",
                    Toast.LENGTH_SHORT).show();
        }
    }


}



