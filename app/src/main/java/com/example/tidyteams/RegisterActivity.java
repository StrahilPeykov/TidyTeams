package com.example.tidyteams;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_FOLDERS = 99;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 15;

    private String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();

        Button registerBtn = findViewById(R.id.RegisterButton);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.RegisterEmail);
        passwordEditText = findViewById(R.id.RegisterPass);
        passwordConfirmEditText = findViewById(R.id.RegisterPassConfirm);
    }

    private void createNewAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToastMessage("Please enter email");
        } else if (TextUtils.isEmpty(password)) {
            showToastMessage("Please enter password");
        } else if (TextUtils.isEmpty(passwordConfirm)) {
            showToastMessage("Please confirm password");
        } else if (!password.equals(passwordConfirm)) {
            showToastMessage("Passwords do not match");
        } else if (password.length() > MAX_PASSWORD_LENGTH || password.length() < MIN_PASSWORD_LENGTH) {
            showToastMessage("Password must be between 8-15 characters");
        } else {
            showLoadingBar("Creating New Account", "Please wait, while we are creating new " +
                    "account for you...");

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                onRegistrationSuccess();
                            } else {
                                onRegistrationFailed(task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoadingBar(String title, String message) {
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);
    }

    private void onRegistrationSuccess() {
        Log.d("TAG", "createUserWithEmail:success");
        FirebaseUser user = mAuth.getCurrentUser();
        showToastMessage("Authentication success.");
        loadingBar.dismiss();
        user.sendEmailVerification();
        ActivityCompat.requestPermissions(RegisterActivity.this,
                permissions,
                PERMISSION_REQUEST_READ_FOLDERS);
    }

    private void onRegistrationFailed(String message) {
        Log.w("TAG", "createUserWithEmail:failure");
        showToastMessage("Authentication failed, error : " + message);
        loadingBar.dismiss();
    }
}