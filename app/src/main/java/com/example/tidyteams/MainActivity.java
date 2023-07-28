package com.example.tidyteams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tidyteams.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ProgressDialog loadingBar;
    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private TextView emailTextView;
    private TextView passwordTextView;

    private Button signInButton;
    private Button forgotPasswordButton;
    private Button noAccountButton;

    SharedPreferences sharedPreferences;
    public static final String LOGIN_PREFS = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        initializeViews();
        setupListeners();
        autoLogin();
    }

    private void initializeViews() {
        emailTextView = findViewById(R.id.LoginEmail);
        passwordTextView = findViewById(R.id.editTextTextPassword);
        signInButton = findViewById(R.id.SignUp);
        forgotPasswordButton = findViewById(R.id.forgotPassword);
        noAccountButton = findViewById(R.id.changeToRegister);
        loadingBar = new ProgressDialog(this);
    }

    private void setupListeners() {
        signInButton.setOnClickListener(view -> loginUser());
        forgotPasswordButton.setOnClickListener(view -> navigateToForgotPassword());
        noAccountButton.setOnClickListener(view -> navigateToRegister());
    }

    private void autoLogin() {
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> navigateToHomeActivity());
        }
    }

    private void loginUser() {
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToast("Please enter email");
        } else if (TextUtils.isEmpty(password)) {
            showToast("Please enter password");
        } else {
            showLoadingBar("Login", "Please wait, while we are checking the credentials.");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            saveUserCredentials(email, password);
                            checkUserExistence();
                        } else {
                            showToast("Login Failed");
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    private void checkUserExistence() {
        final String currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserId)) {
                    navigateToSetupActivity();
                } else {
                    navigateToHomeActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void navigateToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void navigateToHomeActivity() {
        Intent homeIntent = new Intent(MainActivity.this, SplashScreenActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void navigateToForgotPassword() {
        startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
    }

    private void navigateToRegister() {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }

    private void saveUserCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoadingBar(String title, String message) {
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
