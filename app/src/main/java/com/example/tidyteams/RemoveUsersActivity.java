package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class RemoveUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_users);

        //TextView guidelinesLink = findViewById(R.id.guidelines_link);
        TextView userToRemoveEditText = findViewById(R.id.user_to_remove_edittext);
        TextView reasonToRemoveEditText = findViewById(R.id.editTextReason);
        Button saveChangesButton = findViewById(R.id.save_changes_button);


        String guidelinesText = "Here you can remove users from this app. Check our ";
        String linkText = "Guidelines";
        String endText = " for removing users for more.";
        SpannableString spannableString = new SpannableString(guidelinesText + linkText + endText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // Implement the action to open Guidelines screen
                Intent intent = new Intent(RemoveUsersActivity.this, GuidelinesActivity.class);
                startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, guidelinesText.length(),
                (guidelinesText + linkText).length(), 0);

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement the action to save changes and remove the user

                String userToRemove = userToRemoveEditText.getText().toString();
                String reasonToRemove = reasonToRemoveEditText.getText().toString();

                if (!userToRemove.isEmpty() && !reasonToRemove.isEmpty()) {
                    // Perform the removal operation and save changes
                    // ...

                    // Optionally, navigate back to the previous activity or show a confirmation
                    // message
                    // ...
                }
            }
        });
    }
}
