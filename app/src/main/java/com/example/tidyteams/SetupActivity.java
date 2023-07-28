package com.example.tidyteams;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Calendar;

public class SetupActivity extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;

    private String downloadUrl;
    private String currentUserID;
    private EditText userNameEditText, nameEditText, birthdayEditText;
    private Button saveInformationButton;
    private ImageView profileImageView;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageRef;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initializeFirebase();
        initializeViews();
        setOnClickListeners();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users").child(currentUserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
    }

    private void initializeViews() {
        userNameEditText = findViewById(R.id.setup_username);
        nameEditText = findViewById(R.id.setup_full_name);
        birthdayEditText = findViewById(R.id.setup_dob);
        birthdayEditText.setFocusable(false);

        saveInformationButton = findViewById(R.id.setup_information_button);
        profileImageView = findViewById(R.id.profile_image_view);
        loadingBar = new ProgressDialog(this);
    }

    private void setOnClickListeners() {
        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountSetupInformation();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            handleImageUpload(imageUri);
        }
    }

    private void handleImageUpload(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            profileImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetupActivity.this, "Profile Image stored successfully to " +
                            "Firebase storage...", Toast.LENGTH_SHORT).show();
                    downloadUrl =
                            task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                } else {
                    Toast.makeText(SetupActivity.this,
                            "Error Occurred: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveAccountSetupInformation() {
        String username = userNameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String birthday = birthdayEditText.getText().toString();

        if (isValidInput(username, name, birthday)) {
            checkIfUsernameExists(username, name, birthday);
        }
    }

    private boolean isValidInput(String username, String name, String birthday) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username.length() < 5 || username.length() > 15) {
            Toast.makeText(this, "Username must be between 5 and 15 characters long.",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(birthday)) {
            Toast.makeText(this, "Please write your date of birth...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkIfUsernameExists(String username, String name, String birthday) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default" +
                "-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference usersRef = rootRef.child("Users");
        Query query = usersRef.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SetupActivity.this, "Username already exists. Please choose " +
                            "another one.", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserInformation(username, name, birthday);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void saveUserInformation(String username, String name, String birthday) {
        showLoadingBar("Saving Information", "Please wait, while we are creating your new Account" +
                "...");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("name", name);
        userMap.put("birthday", birthday);
        userMap.put("role", "normal");
        userMap.put("gender", "attackHelicopter");

        usersRef.child("profileimage").setValue(downloadUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase " +
                                    "Database Successfully...", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "Error Occured: " + message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendUserToMainActivity();
                    Toast.makeText(SetupActivity.this, "Your account is created successfully.",
                            Toast.LENGTH_LONG).show();
                    hideLoadingBar();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Error Occurred: " + message,
                            Toast.LENGTH_SHORT).show();
                    hideLoadingBar();
                }
            }
        });
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the maximum date to today
        DatePickerDialog datePickerDialog = new DatePickerDialog(SetupActivity.this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                birthdayEditText.setText(date);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Show the date exactly 18 years ago
        Calendar defaultDate = Calendar.getInstance();
        defaultDate.add(Calendar.YEAR, -18);
        datePickerDialog.updateDate(defaultDate.get(Calendar.YEAR),
                defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void showLoadingBar(String title, String message) {
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
    }

    private void hideLoadingBar() {
        loadingBar.dismiss();
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}