// Hunter Wright - Register Page

package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
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

public class RegisterActivity extends AppCompatActivity {
    final String strRegisterURL = "https://pandaexpress-rating-backend-group5.onrender.com/users/register";

    // SharedPreferences to store registration fields
    SharedPreferences registerSharedPrefs;

    // References to EditTexts and Buttons
    TextInputLayout tilRegisterUserName,
            tilRegisterPassword,
            tilConfirmPassword;
    EditText etRegisterUsername,
            etRegisterPassword,
            etConfirmPassword;
    Button btnRegister;
    ProgressBar pbRegister;

    RequestQueue queue; // Asynchronous API calling

    // Boolean values to check if fields are valid
    boolean blnUsername = false,
            blnPassword = false,
            blnConfirmPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get registration fields from SharedPreferences
        registerSharedPrefs = getSharedPreferences("register_fields", MODE_PRIVATE);

        // Initializes volley queue
        queue = Volley.newRequestQueue(this);

        // Get references to EditTexts and Buttons
        getViews();

        // Enables register button if all fields are valid
        nameTextChanged();
        passwordTextChanged();
        confirmPasswordTextChanged();

        // Return to login screen
        btnRegister.setOnClickListener(v -> {
            registerUser(getUser());
        });
    }

    // Registers user with username and password in body
    public void registerUser(User user) {
        // Create JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", user.getStrUsername());
            jsonBody.put("password", user.getStrPassword());    // Password is hashed in backend
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pbRegister.setVisibility(ProgressBar.VISIBLE);
        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, strRegisterURL, jsonBody, response -> {
            try {
                // If registration is successful, return to login screen
                if (response.getString("username").equals(user.getStrUsername())) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    pbRegister.setVisibility(ProgressBar.INVISIBLE);
                }
                // If registration is unsuccessful, display error message
                else {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                pbRegister.setVisibility(ProgressBar.INVISIBLE);
                e.printStackTrace();
            }
        }, error -> {
            pbRegister.setVisibility(ProgressBar.INVISIBLE);
            Log.e("Volley", error.toString() + " :(");
        });

        // Add request to queue
        queue.add(jsonObjectRequest);
        finish();
    }

    // Returns user with username and password
    public User getUser() {
        return new User(etRegisterUsername.getText().toString().toLowerCase(), etRegisterPassword.getText().toString());
    }

    // Get references to EditTexts and Buttons
    public void getViews() {
        tilRegisterUserName = findViewById(R.id.tilRegisterUsername);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        // Remove error icons for passwords
        tilRegisterPassword.setErrorIconDrawable(0);
        tilConfirmPassword.setErrorIconDrawable(0);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etLoginPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        pbRegister = findViewById(R.id.pbRegister);
    }

    // -- EditTexts listeners --
    // Name validation
    public void nameTextChanged() {
        etRegisterUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    blnUsername = true;
                    tilRegisterUserName.setErrorEnabled(false);
                }
                else {
                    blnUsername = false;
                    tilRegisterUserName.setErrorEnabled(true);
                    tilRegisterUserName.setError("Username cannot be empty");
                }

                // Enable register button if all fields are valid
                registerButtonEnabled();
            }
        });
    }

    // Password validation
    public void passwordTextChanged() {
        etRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 8) {
                    tilRegisterPassword.setErrorEnabled(true);
                    tilRegisterPassword.setError("Password must be at least 8 characters");
                }
                else if (s.toString().contains(" ")) {
                    tilRegisterPassword.setErrorEnabled(true);
                    tilRegisterPassword.setError("Password cannot contain spaces");
                }
                else {
                    tilRegisterPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Password validation
                if (s.toString().length() >= 8 && !s.toString().contains(" ") && !s.toString().isEmpty()) {
                    blnPassword = true;
                    tilRegisterPassword.setErrorEnabled(false);
                } else {
                    blnPassword = false;
                    tilRegisterPassword.setErrorEnabled(true);
                    tilRegisterPassword.setError("Password must be at least 8 characters");
                }

                // Password matching confirmation
                if (s.toString().equals(etConfirmPassword.getText().toString())) {
                    blnConfirmPassword = true;
                    tilRegisterPassword.setErrorEnabled(false);
                } else {
                    blnConfirmPassword = false;
                    tilRegisterPassword.setErrorEnabled(true);
                    tilConfirmPassword.setError("Passwords do not match");
                }

                // Enable register button if all fields are valid
                registerButtonEnabled();
            }
        });
    }

    // Password matching confirmation
    public void confirmPasswordTextChanged() {
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(etRegisterPassword.getText().toString())) {
                    tilConfirmPassword.setError(null);
                } else {
                    tilConfirmPassword.setError("Passwords do not match");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(etRegisterPassword.getText().toString())) {
                    blnConfirmPassword = true;
                } else {
                    blnConfirmPassword = false;
                    tilConfirmPassword.setError("Passwords do not match");
                }

                // Enable register button if all fields are valid
                registerButtonEnabled();
            }
        });
    }

    // Enables register button if all fields are valid
    public void registerButtonEnabled() {
        if (blnUsername && blnPassword && blnConfirmPassword)
            btnRegister.setEnabled(true);
        else
            btnRegister.setEnabled(false);
    }

    // Save registration fields to SharedPreferences
    public void saveRegisterFields() {
        SharedPreferences.Editor editor = registerSharedPrefs.edit();
        editor.putString("username", etRegisterUsername.getText().toString());
        editor.putString("password", etRegisterPassword.getText().toString());
        editor.apply();
    }

    // Load registration fields from SharedPreferences
    public void loadRegisterFields() {
        // Load registration fields from SharedPreferences
        String strUsername = registerSharedPrefs.getString("username", "");
        String strPassword = registerSharedPrefs.getString("password", "");

        // If registration fields are empty, return
        if (strUsername.isEmpty() || strPassword.isEmpty())
            return;

        // Set registration fields
        etRegisterUsername.setText(strUsername);
        etRegisterPassword.setText(strPassword);
    }

    // Saves register fields to SharedPreferences
    @Override
    protected void onPause() {
        super.onPause();

        // Save registration fields to SharedPreferences
        saveRegisterFields();
    }

    // Loads register fields from SharedPreferences
    @Override
    protected void onResume() {
        super.onResume();

        // Load registration fields from SharedPreferences
        loadRegisterFields();
    }

}