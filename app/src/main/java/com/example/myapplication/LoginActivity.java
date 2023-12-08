// Hunter Wright - Login Page
package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    final String strLoginURL = "https://pandaexpress-rating-backend-group5.onrender.com/users/login"; // URL to login API

    SharedPreferences loginSharedPrefs; // SharedPreferences to store login fields and auth cookies
    SharedPreferences savedSessionSharedPrefs; // SharedPreferences to store saved session information
    SessionManager sessionManager; // SessionManager to store session

    // References to EditTexts, CheckBox, and Buttons
    TextInputLayout tilLoginUsername, tilLoginPassword;
    EditText etLoginUsername,  etLoginPassword;
    Button btnLogin, btnRegister;
    CheckBox cbRemember;
    ProgressBar pbLogin;

    ExecutorService executorService; // ExecutorService to run threads
    CookieManager cookieManager; // CookieManager to store cookies
    RequestQueue queue; // Asynchronous API calling

    // Booleans to check if email and password are valid
    boolean blnUsername = false, blnPassword = false;

    boolean blnRemember = false; // Boolean to check if Remember Me is selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get login fields from SharedPreferences
        loginSharedPrefs = getSharedPreferences("login_fields", MODE_PRIVATE);
        savedSessionSharedPrefs = getSharedPreferences("saved_session", MODE_PRIVATE);

        sessionManager = new SessionManager(this); // Initializes session manager

        // Initializes volley queue
        queue = Volley.newRequestQueue(this.getApplicationContext());

        // ExecutorService to run threads
        executorService = Executors.newSingleThreadExecutor();

        // CookieManager to store cookies
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // Get references to EditTexts, CheckBox, and Buttons
        getViews();

        if (sessionManager.hasSavedSessionInformation()) {
            // If yes, redirect to the home screen
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Close the current activity
        }

        // Get intent extra
        Intent intent = getIntent();
        boolean blnLogout = intent.getBooleanExtra("signOut", false);

        // Check if user is signed out
        if (blnLogout) {
            Log.d("wowza", "User signed out");

            // Clear session
            sessionManager.clearSession();

            // Clear login fields
            loginSharedPrefs.edit().putString("email", "").apply();
            loginSharedPrefs.edit().putString("password", "").apply();


            // Clear email and password from EditText
            etLoginUsername.setText("");
            etLoginPassword.setText("");

            cbRemember.setChecked(false);
        }

        // Check location permissions
        checkLocationPermissions();

        // Enables login button if both email and password are valid
        nameTextChanged();
        passwordChanged();

        cbRemember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                blnRemember = true;
            else
                blnRemember = false;
        });

        // Checks if email and password match a user
        btnLogin.setOnClickListener(v -> {
            login(getUser());
        });

        // Go to RegisterActivity
        btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });
    }

    // Check location permissions
    public void checkLocationPermissions() {
        // Check if location permissions are granted
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED)
            // Request location permissions
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    // Get references to EditTexts, CheckBox, and Buttons
    public void getViews() {
        tilLoginUsername = findViewById(R.id.tilLoginUsername);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);

        // Remove error icons for password
        tilLoginPassword.setErrorIconDrawable(0);

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        cbRemember = findViewById(R.id.cbRemember);

        pbLogin = findViewById(R.id.pbLogin);
    }

    // Username validation
    public void nameTextChanged() {
        // addTextChangedListener that is called when the text is changed
        etLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // Check if username is valid after text is changed
            @Override
            public void afterTextChanged(Editable s) {
                // Check if username is valid
                if (s.toString().length() >= 1 && !s.toString().isEmpty() && !s.toString().contains(" ")) {
                    tilLoginUsername.setErrorEnabled(false);
                    blnUsername = true;
                }
                else {
                    tilLoginUsername.setError("Username cannot be empty or contain spaces");
                    tilLoginUsername.setErrorEnabled(true);
                    blnUsername = false;
                }

                // Enable login button if both email and password are valid
                loginButtonEnabled();
            }
        });
    }

    // Password validation
    public void passwordChanged() {
        // addTextChangedListener that is called when the text is changed
        etLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            // Check if password is valid after text is changed
            @Override
            public void afterTextChanged(Editable s) {
                // Check if password is valid
                if (s.toString().length() >= 1 && !s.toString().isEmpty() && !s.toString().contains(" ")) {
                    tilLoginPassword.setErrorEnabled(false);
                    blnPassword = true;
                }
                else {
                    tilLoginPassword.setError("Password cannot be empty or contain spaces");
                    tilLoginPassword.setErrorEnabled(true);
                    blnPassword = false;
                }

                // Enable login button if both email and password are valid
                loginButtonEnabled();
            }
        });
    }

    public void loginButtonEnabled() {
        // Enable login button if both email and password are valid
        if (blnUsername && blnPassword)
            btnLogin.setEnabled(true);
        else
            btnLogin.setEnabled(false);
    }

    // Calls login URL and checks if email and password match a user
    public void login(User user) {
        // Create JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", user.getStrUsername());
            jsonBody.put("password", user.getStrPassword());    // Password is hashed in backend
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pbLogin.setVisibility(ProgressBar.VISIBLE);

        // Create POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, strLoginURL, jsonBody, response -> {
            try {
                // Get response from API
                String strResponse = response.getString("message");

                // Display message
                Toast.makeText(this, strResponse, Toast.LENGTH_SHORT).show();

                // Check if response is success
                if (strResponse.contains("Successfully logged in")) {
                    pbLogin.setVisibility(ProgressBar.INVISIBLE); // Hide progress bar

                    String strUserName = response.getString("username");
                    String strUserID = response.getString("user_id");

                    // Save session information
                    savedSessionSharedPrefs.edit().putString("sessionToken", strUserID).apply();
                    savedSessionSharedPrefs.edit().putString("user_id", strUserID).apply();

                    // Go to MainActivity
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.putExtra("username", strUserName);
                    i.putExtra("user_id", strUserID);
                    startActivity(i);
                }

            } catch (JSONException e) {
                pbLogin.setVisibility(ProgressBar.INVISIBLE);
                e.printStackTrace();
            }
        }, error -> {
            // Display error message
            Toast.makeText(this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();

            pbLogin.setVisibility(ProgressBar.INVISIBLE);
            Log.e("Volley", error.toString());
        });

        // Add request to queue
        queue.add(jsonObjectRequest);
    }

    // Return a user instance
    public User getUser() {
        return new User(etLoginUsername.getText().toString().toLowerCase(), etLoginPassword.getText().toString());
    }

//    // Uses bcrypt to hash password and return hashed password
//    public String hashPassword(String strPassword) {
//        return BCrypt.withDefaults().hashToString(12, strPassword.toCharArray());
//    }
//
//    // Uses bcrypt to verify password and return true if password matches hashed password
//    public boolean verifyPassword(String strPassword, String strBcryptPassword) {
//        BCrypt.Result result = BCrypt.verifyer().verify(strPassword.toCharArray(), strBcryptPassword);
//        return result.verified;
//    }

    // Save login fields to SharedPreferences
    public void saveLoginFields() {
        // Get email and password from EditText
        String strEmail = etLoginUsername.getText().toString();
        String strPassword = etLoginPassword.getText().toString();

        // Check cbRemember
        if (cbRemember.isChecked())
            blnRemember = true;
        else
            blnRemember = false;

        // Check if email and password are empty
        if (strEmail.isEmpty() || strPassword.isEmpty())
            return;

        // Check if Remember Me is selected
        if (blnRemember) {
            // Save email and password to SharedPreferences
            loginSharedPrefs.edit().putString("email", strEmail).apply();
            loginSharedPrefs.edit().putString("password", strPassword).apply();
        }
        else {
            // Clear email and password from SharedPreferences
            loginSharedPrefs.edit().putString("email", "").apply();
            loginSharedPrefs.edit().putString("password", "").apply();
        }
    }

    // Load login fields from SharedPreferences
    public void loadLoginFields() {
        // Check if loginSharedPrefs is null
        if (loginSharedPrefs == null)
            return;

        // Get email and password from SharedPreferences
        String strEmail = loginSharedPrefs.getString("email", "");
        String strPassword = loginSharedPrefs.getString("password", "");

        if (cbRemember.isChecked())
            cbRemember.setChecked(false);
        else
            cbRemember.setChecked(true);

        if (strEmail.isEmpty() || strPassword.isEmpty())
            return;

        // Set email and password to EditText
        etLoginUsername.setText(strEmail);
        etLoginPassword.setText(strPassword);
    }

    // Saves login fields to SharedPreferences
    @Override
    protected void onPause() {
        super.onPause();

        // Save login fields to SharedPreferences
        saveLoginFields();
    }

    // Loads login fields from SharedPreferences
    @Override
    protected void onResume() {
        super.onResume();

        // Load login fields from SharedPreferences
        loadLoginFields();
    }
}