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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeNameActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Button confirmChangeNameButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users").child(currentUserID);

        nameEditText = findViewById(R.id.edit_name);
        confirmChangeNameButton = findViewById(R.id.confirm_change_name);

        String currentName = getIntent().getStringExtra("current_name");
        if (currentName != null) {
            nameEditText.setText(currentName);
        }
        confirmChangeNameButton.setOnClickListener(view -> updateName());
    }

    private void updateName() {
        String newName = nameEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(newName)) {
            usersRef.child("name").setValue(newName)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ChangeNameActivity.this, "Name changed successfully!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ChangeNameActivity.this, "Error " +
                            "occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(ChangeNameActivity.this, "Please enter a new name",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
