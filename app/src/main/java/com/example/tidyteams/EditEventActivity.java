package com.example.tidyteams;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class EditEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        String eventId = getIntent().getStringExtra("event_id");
        boolean isCreateMode = getIntent().getBooleanExtra("is_create_mode", false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        EventDetailsFragment eventDetailsFragment = EventDetailsFragment.newInstance(isCreateMode
                , eventId, !isCreateMode);
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, eventDetailsFragment)
                .commit();
    }

}

