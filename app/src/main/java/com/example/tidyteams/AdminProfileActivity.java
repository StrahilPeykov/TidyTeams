package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {
        Button createEventButton = findViewById(R.id.create_event_button);
        Button manageEventButton = findViewById(R.id.manage_event_button);
        Button removeUsersButton = findViewById(R.id.remove_users_button);

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateEventActivity();
            }
        });

        manageEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openYourEventsActivity();
            }
        });

        removeUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRemoveUsersActivity();
            }
        });
    }

    private void openCreateEventActivity() {
        Intent intent = new Intent(AdminProfileActivity.this, CreateEventActivity.class);
        startActivity(intent);
    }

    private void openYourEventsActivity() {
        Intent intent = new Intent(AdminProfileActivity.this, YourEventsActivity.class);
        startActivity(intent);
    }

    private void openRemoveUsersActivity() {
        Intent intent = new Intent(AdminProfileActivity.this, RemoveUsersActivity.class);
        startActivity(intent);
    }
}
