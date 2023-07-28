package com.example.tidyteams;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

public class CreateEventActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_FOLDERS = 98;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        SharedPreferences pref = this.getSharedPreferences("PACKAGE.NAME", MODE_PRIVATE);
        Boolean firstTime = pref.getBoolean("firstTime", true);
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (firstTime) {
            // Ask for permissions here
            pref.edit().putBoolean("firstTime", false).apply();
            ActivityCompat.requestPermissions(CreateEventActivity.this,
                    PERMISSIONS,
                    PERMISSION_REQUEST_READ_FOLDERS);
        }

        String eventId = getIntent().getStringExtra("event_id");
        boolean isCreateMode = getIntent().getBooleanExtra("is_create_mode", false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        EventDetailsFragment eventDetailsFragment = EventDetailsFragment.newInstance(true,
                eventId, false);
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, eventDetailsFragment)
                .commit();
    }
}
