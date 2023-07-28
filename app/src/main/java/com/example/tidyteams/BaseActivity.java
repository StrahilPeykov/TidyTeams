package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected ImageView personButton, mapButton, homeButton, notificationsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupTopMenu() {
        personButton = findViewById(R.id.icon_person);
        mapButton = findViewById(R.id.icon_map);
        homeButton = findViewById(R.id.icon_home);
        notificationsButton = findViewById(R.id.icon_notifications);

        personButton.setOnClickListener(createNavigationClickListener(YourProfileActivity.class));
        mapButton.setOnClickListener(createNavigationClickListener(MapActivity.class));
        homeButton.setOnClickListener(createNavigationClickListener(HomePageActivity.class));
        notificationsButton.setOnClickListener(createNavigationClickListener(NotificationsActivity.class));
    }

    private View.OnClickListener createNavigationClickListener(final Class<?> targetActivity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(targetActivity);
            }
        };
    }

    protected void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    protected void setSelectedIcon(ImageView selectedIcon) {
        if (selectedIcon != null) {
            selectedIcon.setColorFilter(Color.parseColor("#64B5F6"));
            selectedIcon.setClickable(false);
        }
    }
}
