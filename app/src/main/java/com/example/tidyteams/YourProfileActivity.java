package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class YourProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);
        setupTopMenu();
        setSelectedIcon(personButton);
        setupProfileButtons();
    }

    private void setupProfileButtons() {
        Button settingsButton = findViewById(R.id.settings_button);
        Button editProfileButton = findViewById(R.id.edit_profile_button);
        Button aboutTidyTeamsButton = findViewById(R.id.about_tidyteams_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(SettingsActivity.class);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(EditProfileActivity.class);
            }
        });

        aboutTidyTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(AboutTidyTeamsActivity.class);
            }
        });
    }
}
