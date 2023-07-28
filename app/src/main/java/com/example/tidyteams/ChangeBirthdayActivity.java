package com.example.tidyteams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ChangeBirthdayActivity extends AppCompatActivity {

    private EditText birthdayEditText;
    private Button confirmChangeBirthdayButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_birthday);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users").child(currentUserID);

        birthdayEditText = findViewById(R.id.edit_birthday);
        confirmChangeBirthdayButton = findViewById(R.id.confirm_change_birthday);

        // Read the current birthday from the intent and set it as the text in the EditText
        String currentBirthday = getIntent().getStringExtra("current_birthday");
        if (currentBirthday != null) {
            birthdayEditText.setText(currentBirthday);
        }

        birthdayEditText.setOnClickListener(view -> showDatePickerDialog());
        confirmChangeBirthdayButton.setOnClickListener(view -> updateBirthday());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate =
                            selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    birthdayEditText.setText(selectedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateBirthday() {
        String newBirthday = birthdayEditText.getText().toString();

        if (!newBirthday.isEmpty()) {
            usersRef.child("birthday").setValue(newBirthday)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ChangeBirthdayActivity.this, "Birthday changed " +
                                "successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ChangeBirthdayActivity.this, "Error" +
                            " occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(ChangeBirthdayActivity.this, "Please enter a new birthday",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
