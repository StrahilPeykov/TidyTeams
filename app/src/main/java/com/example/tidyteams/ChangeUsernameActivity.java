package com.example.tidyteams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChangeUsernameActivity extends AppCompatActivity {

    private EditText editUsernameEditText;
    private Button confirmChangeUsernameButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users").child(currentUserID);

        editUsernameEditText = findViewById(R.id.edit_username);
        confirmChangeUsernameButton = findViewById(R.id.confirm_change_username);

        String currentUsername = getIntent().getStringExtra("current_username");
        if (currentUsername != null) {
            editUsernameEditText.setText(currentUsername);
        }
        confirmChangeUsernameButton.setOnClickListener(view -> updateUsername());
    }

    private void updateUsername() {
        String newUsername = editUsernameEditText.getText().toString().trim();

        if (isUsernameValid(newUsername)) {
            checkIfUsernameExistsAndChange(newUsername);
        }
    }

    private boolean isUsernameValid(String newUsername) {
        if (TextUtils.isEmpty(newUsername)) {
            Toast.makeText(ChangeUsernameActivity.this, "Please enter a new username",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newUsername.length() < 5 || newUsername.length() > 15) {
            Toast.makeText(ChangeUsernameActivity.this, "Username must be between 5 and 15 " +
                    "characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkIfUsernameExistsAndChange(String newUsername) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default" +
                "-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference usersRef = rootRef.child("Users");
        Query query = usersRef.orderByChild("username").equalTo(newUsername);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(ChangeUsernameActivity.this, "Username already exists. Please " +
                            "choose another one.", Toast.LENGTH_SHORT).show();
                } else {
                    changeUsernameInDatabase(newUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void changeUsernameInDatabase(String newUsername) {
        userReference.child("username").setValue(newUsername)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChangeUsernameActivity.this, "Username changed successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ChangeUsernameActivity.this, "Error " +
                        "occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
