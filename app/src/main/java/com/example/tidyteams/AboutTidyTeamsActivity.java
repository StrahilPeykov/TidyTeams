package com.example.tidyteams;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutTidyTeamsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_tidy_teams);
        initializeAboutTextView();
    }

    private void initializeAboutTextView() {
        TextView aboutTidyTeamsTextView = findViewById(R.id.about_description);
        String aboutText = getAboutText();
        aboutTidyTeamsTextView.setText(aboutText);
    }

    private String getAboutText() {
        return "Welcome to TidyTeams, the innovative app designed to connect environmentally " +
                "conscious individuals who are passionate about making a positive impact in their" +
                " communities. Our app provides a platform for like-minded people to plan and " +
                "join clean-up events together at public parks and beaches. We noticed a problem " +
                "that many environmentally conscious individuals faced - the lack of a dedicated " +
                "platform to easily find each other and plan clean-up events. TidyTeams addresses" +
                " this issue by providing a user-friendly app that connects people with a shared " +
                "interest in cleaning up their communities. Our team is a mix of experience and " +
                "expertise, consisting of computer scientists who are well-versed in programming " +
                "concepts and familiar with Android Studio. Although none of us had extensive " +
                "experience in app development specifically, we were highly motivated to put in " +
                "the time and effort required to learn. Each team member brought unique strengths" +
                " and experiences to the table, allowing us to effectively develop and deliver a " +
                "high-quality app. At TidyTeams, we provide a fun and social way for " +
                "environmentally conscious individuals to participate in clean-up events. Our app" +
                " is the perfect solution for those who want to make a positive impact in their " +
                "communities while connecting with like-minded individuals. Join us today and " +
                "let's work together to keep our environment clean and green!";
    }
}
