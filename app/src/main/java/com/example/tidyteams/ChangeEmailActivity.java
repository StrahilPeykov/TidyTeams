package com.example.tidyteams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button confirmChangeEmailButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users").child(currentUserID);

        emailEditText = findViewById(R.id.edit_email);
        confirmChangeEmailButton = findViewById(R.id.confirm_change_email);

        String currentEmail = getIntent().getStringExtra("current_email");
        if (currentEmail != null) {
            emailEditText.setText(currentEmail);
        }
        confirmChangeEmailButton.setOnClickListener(view -> updateEmail());
    }

    private void updateEmail() {
        String newEmail = emailEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(newEmail)) {
            FirebaseUser user = mAuth.getCurrentUser();
            user.updateEmail(newEmail)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ChangeEmailActivity.this, "Verification email sent to new " +
                                "email, please verify and login again.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut(); // sign out the user to force re-login after email
                        // verification
                        sendEmailVerification(newEmail);
                    })
                    .addOnFailureListener(e -> Toast.makeText(ChangeEmailActivity.this, "Error " +
                            "occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(ChangeEmailActivity.this, "Please enter a new email",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailVerification(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChangeEmailActivity.this,
                            "Verification email sent to " + newEmail, Toast.LENGTH_SHORT).show();
                    finish(); // finish the activity to prevent going back to it after sign out
                })
                .addOnFailureListener(e -> Toast.makeText(ChangeEmailActivity.this, "Error " +
                        "occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
