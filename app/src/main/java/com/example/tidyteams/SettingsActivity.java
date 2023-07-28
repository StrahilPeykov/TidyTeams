package com.example.tidyteams;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String currentUserID;
    private String userRole;

    private Button logOutButton;
    private Button adminProfileButton;
    private Button deleteButton;

    public class Users {
        private String admin;
        private String birthday;
        private String name;
        private String gender;
        private String username;
    }

    public String getIsAdmin() {
        return userRole;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersReference = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users").child(currentUserID);

        initializeViews();
        setupListeners();
        getUserRole();
    }

    private void initializeViews() {
        logOutButton = findViewById(R.id.logout_button);
        adminProfileButton = findViewById(R.id.admin_profile_button);
        deleteButton = findViewById(R.id.delete_account_button);
    }

    private void setupListeners() {
        logOutButton.setOnClickListener(view -> logOut());
        deleteButton.setOnClickListener(view -> showDeleteAccountDialog());
        adminProfileButton.setOnClickListener(view -> navigateToAdminProfile());
    }

    private void logOut() {
        clearUserData();
        mAuth.signOut();
        navigateToLoginActivity();
    }

    private void clearUserData() {
        SharedPreferences userData = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = userData.edit();
        editor1.clear();
        editor1.apply();

        SharedPreferences loginPrefs = getSharedPreferences(MainActivity.LOGIN_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginPrefs.edit();
        editor.clear();
        editor.commit();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
        dialog.setTitle("Are you sure?");
        dialog.setMessage("Deleting your account means that your user information will be removed" +
                " from the Database, " +
                "This action is not reversible. Make sure you are certain before deleting your " +
                "account");
        dialog.setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUserAccount();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void deleteUserAccount() {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this,
                            "Your account has been deleted", Toast.LENGTH_SHORT).show();
                    navigateToLoginActivity();
                } else {
                    Toast.makeText(SettingsActivity.this,
                            "Your account could not be deleted: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void navigateToAdminProfile() {
        Intent intent = new Intent(SettingsActivity.this, AdminProfileActivity.class);
        startActivity(intent);
    }

    private void getUserRole() {
        usersReference.child("role").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userRole = snapshot.getValue(String.class);
                    updateAdminProfileButtonVisibility();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void updateAdminProfileButtonVisibility() {
        if ("admin".equalsIgnoreCase(userRole)) {
            adminProfileButton.setVisibility(View.VISIBLE);
        } else {
            adminProfileButton.setVisibility(View.GONE);
        }
    }
}