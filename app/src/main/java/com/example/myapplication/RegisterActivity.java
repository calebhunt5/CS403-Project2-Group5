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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    // SharedPreferences to store registration fields
    SharedPreferences registerSharedPrefs;
    TextInputLayout tilRegisterName,
            tilRegisterEmail,
            tilRegisterPassword,
            tilConfirmPassword;
    EditText etRegisterName,
            etRegisterEmail,
            etRegisterPassword,
            etConfirmPassword;
    Button btnRegister;
    boolean blnName = false,
            blnEmail = false,
            blnPassword = false,
            blnConfirmPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get registration fields from SharedPreferences
        registerSharedPrefs = getSharedPreferences("register_fields", MODE_PRIVATE);

        // Get references to EditTexts and Buttons
        getViews();

        // Enables register button if all fields are valid
        nameTextChanged();
        emailTextChanged();
        passwordTextChanged();
        confirmPasswordTextChanged();

        // Return to login screen
        btnRegister.setOnClickListener(v -> {
            finish();
        });
    }

    // Get references to EditTexts and Buttons
    public void getViews() {
        tilRegisterName = findViewById(R.id.tilRegisterName);
        tilRegisterEmail = findViewById(R.id.tilRegisterEmail);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        // Remove error icons for passwords
        tilRegisterPassword.setErrorIconDrawable(0);
        tilConfirmPassword.setErrorIconDrawable(0);

        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etLoginPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
    }

    // -- EditTexts listeners --
    // Name validation
    public void nameTextChanged() {
        etRegisterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    blnName = true;
                    tilRegisterName.setError(null);
                    tilRegisterName.setErrorEnabled(false);
                }
                else {
                    blnName = false;
                    tilRegisterName.setErrorEnabled(true);
                    tilRegisterName.setError("Name cannot be empty");
                }

                // Enable register button if all fields are valid
                registerButtonEnabled();
            }
        });
    }

    // Email validation
    public void emailTextChanged() {
        etRegisterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("@") && s.toString().contains(".") && !s.toString().isEmpty() && !s.toString().contains(" ")) {
                    blnEmail = true;
                    tilRegisterEmail.setError(null);
                    tilRegisterEmail.setErrorEnabled(false);
                }
                else if (s.toString().isEmpty()) {
                    blnEmail = false;
                    tilRegisterEmail.setErrorEnabled(true);
                    tilRegisterEmail.setError("Email cannot be empty");
                }
                else if (s.toString().contains(" ")) {
                    blnEmail = false;
                    tilRegisterEmail.setErrorEnabled(true);
                    tilRegisterEmail.setError("Email cannot contain spaces");
                }
                else if (!s.toString().contains("@") || !s.toString().contains(".")) {
                    blnEmail = false;
                    tilRegisterEmail.setErrorEnabled(true);
                    tilRegisterEmail.setError("Email is invalid");
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
                    tilRegisterPassword.setError(null);
                    tilRegisterPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Password validation
                if (s.toString().length() >= 8 && !s.toString().contains(" ")) {
                    blnPassword = true;
                } else {
                    blnPassword = false;
                }

                // Password matching confirmation
                if (s.toString().equals(etConfirmPassword.getText().toString())) {
                    blnConfirmPassword = true;
                    tilConfirmPassword.setError(null);
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
        if (blnName && blnEmail && blnPassword && blnConfirmPassword)
            btnRegister.setEnabled(true);
        else
            btnRegister.setEnabled(false);
    }

    // Save registration fields to SharedPreferences
    public void saveRegisterFields() {
        SharedPreferences.Editor editor = registerSharedPrefs.edit();
        editor.putString("name", etRegisterName.getText().toString());
        editor.putString("email", etRegisterEmail.getText().toString());
        editor.putString("password", etRegisterPassword.getText().toString());
        editor.apply();
    }

    // Load registration fields from SharedPreferences
    public void loadRegisterFields() {
        // Load registration fields from SharedPreferences
        String strName = registerSharedPrefs.getString("name", "");
        String strEmail = registerSharedPrefs.getString("email", "");
        String strPassword = registerSharedPrefs.getString("password", "");

        // If registration fields are empty, return
        if (strName.isEmpty() || strEmail.isEmpty() || strPassword.isEmpty())
            return;

        // Set registration fields
        etRegisterName.setText(strName);
        etRegisterEmail.setText(strEmail);
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