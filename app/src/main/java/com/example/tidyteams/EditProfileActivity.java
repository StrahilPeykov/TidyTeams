package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputEditText nameEditText;
    private TextInputEditText usernameEditText;
    private TextInputEditText birthdayEditText;
    private TextInputEditText passwordEditText;
    private Button toggleRoleButton;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String currentUserID;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeComponents();
        fetchUserInfo();
        setupOnClickListeners();
    }

    private void initializeComponents() {
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users").child(currentUserID);

        nameEditText = findViewById(R.id.edit_name);
        usernameEditText = findViewById(R.id.edit_username);
        birthdayEditText = findViewById(R.id.edit_birthday);
        emailEditText = findViewById(R.id.edit_email);
        passwordEditText = findViewById(R.id.edit_password);
        toggleRoleButton = findViewById(R.id.become_admin);
    }

    private void fetchUserInfo() {
        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateUIWithDataSnapshot(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this,
                        "Error occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithDataSnapshot(DataSnapshot dataSnapshot) {
        nameEditText.setText(getValueFromDataSnapshot(dataSnapshot, "name"));
        usernameEditText.setText(getValueFromDataSnapshot(dataSnapshot, "username"));
        birthdayEditText.setText(getValueFromDataSnapshot(dataSnapshot, "birthday"));
        emailEditText.setText(getEmailFromAuth());
        passwordEditText.setText(getValueFromDataSnapshot(dataSnapshot, "password"));
        userRole = getValueFromDataSnapshot(dataSnapshot, "role");
        updateToggleRoleButtonText();
    }

    private String getValueFromDataSnapshot(DataSnapshot dataSnapshot, String key) {
        if (dataSnapshot.hasChild(key)) {
            return dataSnapshot.child(key).getValue().toString();
        }
        return null;
    }

    private String getEmailFromAuth() {
        return mAuth.getCurrentUser().getEmail();
    }

    private void updateToggleRoleButtonText() {
        if ("admin".equalsIgnoreCase(userRole)) {
            toggleRoleButton.setText("BECOME A USER");
        } else {
            toggleRoleButton.setText("BECOME AN ADMIN");
        }
    }

    private void setupOnClickListeners() {
        emailEditText.setOnClickListener(view -> startChangeInfoActivity(ChangeEmailActivity.class, emailEditText, "current_email"));
        nameEditText.setOnClickListener(view -> startChangeInfoActivity(ChangeNameActivity.class,
                nameEditText, "current_name"));
        usernameEditText.setOnClickListener(view -> startChangeInfoActivity(ChangeUsernameActivity.class, usernameEditText, "current_username"));
        birthdayEditText.setOnClickListener(view -> startChangeInfoActivity(ChangeBirthdayActivity.class, birthdayEditText, "current_birthday"));
        passwordEditText.setOnClickListener(view -> {
            Intent changePasswordIntent = new Intent(EditProfileActivity.this,
                    ChangePasswordActivity.class);
            startActivity(changePasswordIntent);
        });
        toggleRoleButton.setOnClickListener(view -> toggleUserRole());
    }

    private void startChangeInfoActivity(Class<?> targetActivity, TextInputEditText editText,
                                         String extraKey) {
        Intent changeInfoIntent = new Intent(EditProfileActivity.this, targetActivity);
        String currentValue = editText.getText().toString();
        changeInfoIntent.putExtra(extraKey, currentValue);
        startActivity(changeInfoIntent);
    }

    private void toggleUserRole() {
        String newRole = "admin".equalsIgnoreCase(userRole) ? "normal" : "admin";
        UsersRef.child("role").setValue(newRole)
                .addOnSuccessListener(aVoid -> {
                    userRole = newRole;
                    updateToggleRoleButtonText();
                    Toast.makeText(EditProfileActivity.this, "Your role has been changed!",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Error " +
                        "occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}