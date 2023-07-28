package com.example.tidyteams;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        oldPasswordEditText = findViewById(R.id.oldPass);
        newPasswordEditText = findViewById(R.id.newPass);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPass);
        Button confirmChangeButton = findViewById(R.id.confirmChange);

        confirmChangeButton.setOnClickListener(view -> updatePassword());
    }

    private void updatePassword() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmNewPasswordEditText.getText().toString();

        if (isInputValid(oldPassword, newPassword, confirmPassword)) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Password updated " +
                                "successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(ChangePasswordActivity.this,
                                "Error updating password: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isInputValid(String oldPassword, String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "Please enter old password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm new password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
