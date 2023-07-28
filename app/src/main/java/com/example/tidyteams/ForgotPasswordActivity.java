package com.example.tidyteams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.edit_email);
        resetPasswordButton = findViewById(R.id.confirm_reset_password);

        resetPasswordButton.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {
        String userEmail = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(ForgotPasswordActivity.this, "Please enter your registered email " +
                    "address.", Toast.LENGTH_SHORT).show();
        } else {
            sendPasswordResetEmail(userEmail);
        }
    }

    private void sendPasswordResetEmail(String userEmail) {
        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email " +
                                    "sent. Please check your email.", Toast.LENGTH_SHORT).show();
                            redirectToLoginActivity();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error occurred: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redirectToLoginActivity() {
        Intent loginIntent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }
}
