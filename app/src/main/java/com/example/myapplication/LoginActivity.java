// Hunter Wright - Login Page
package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    // SharedPreferences to store login fields
    SharedPreferences loginSharedPrefs;

    TextInputLayout tilLoginEmail, tilLoginPassword;
    EditText etLoginEmail,  etLoginPassword;
    CheckBox chkRemember;
    Button btnLogin, btnRegister;

    // ExecutorService to run threads
    ExecutorService executorService;

    // Booleans to check if email and password are valid
    boolean blnEmail = false, blnPassword = false;

    // Store list of dummy users of class User
    List<User> lstUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get login fields from SharedPreferences
        loginSharedPrefs = getSharedPreferences("login_fields", MODE_PRIVATE);

        // ExecutorService to run threads
        executorService = Executors.newSingleThreadExecutor();

        // Get references to EditTexts, CheckBox, and Buttons
        getViews();

        // Create dummy users
        createUsers();

        // Clear fields if checkbox is unchecked
        rememeberFields();

        // Enables login button if both email and password are valid
        emailTextChanged();
        passwordChanged();

        // Stay logged in
        // TODO - Save user instance
        chkRemember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                Toast.makeText(this, "Remember me :)", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Don't remember me :(", Toast.LENGTH_SHORT).show();
        });

        // Checks if email and password match a user
        btnLogin.setOnClickListener(v -> {
            login();
        });

        // Go to RegisterActivity
        btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });
    }

    // Get references to EditTexts, CheckBox, and Buttons
    public void getViews() {
        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);

        // Remove error icons for password
        tilLoginPassword.setErrorIconDrawable(0);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        chkRemember = findViewById(R.id.chkRemember);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    // Email validation
    public void emailTextChanged() {
        // addTextChangedListener that is called when the text is changed
        etLoginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // Check if email is valid after text is changed
            @Override
            public void afterTextChanged(Editable s) {
                // Check if email is valid
                if (s.toString().contains("@") && s.toString().contains(".") && !s.toString().isEmpty() && !s.toString().contains(" ")) {
                    blnEmail = true;
                    tilLoginEmail.setError(null);
                }
                else {
                    blnEmail = false;
                    tilLoginEmail.setError("Email is invalid");
                }

                // Enable login button if both email and password are valid
                if (blnEmail && blnPassword)
                    btnLogin.setEnabled(true);
                else
                    btnLogin.setEnabled(false);
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
                if (s.toString().length() >= 1 && !s.toString().isEmpty() && !s.toString().contains(" "))
                    blnPassword = true;
                else
                    blnPassword = false;

                // Enable login button if both email and password are valid
                if (blnEmail && blnPassword)
                    btnLogin.setEnabled(true);
                else
                    btnLogin.setEnabled(false);
            }
        });
    }

    // Check if email and password match a user
    public void login() {
        // Get email and password from EditText
        String strEmail = etLoginEmail.getText().toString();
        String strPassword = etLoginPassword.getText().toString();

        boolean blnPasswordMatch;   // Check if password matches hashed password
        boolean blnNotFound = false;// Check if email and password match a user

        // Check if email and password are valid
        if (blnEmail && blnPassword) {
            // Check if email and password match a user
            for (User user : lstUsers) {
                // Check if password matches hashed password
                blnPasswordMatch = verifyPassword(strPassword, user.getStrPassword());

                if (user.getStrEmail().equals(strEmail) && blnPasswordMatch) {
                    blnNotFound = false;

                    // Login successful
                    Intent i = new Intent(LoginActivity.this, LaunchActivity.class);
                    startActivity(i);
                    finish();
                    break;
                }
                else
                    blnNotFound = true;
            }
        }

        if (blnNotFound)
            Toast.makeText(this, "Email or password is incorrect", Toast.LENGTH_LONG).show();
    }

    // Uses bcrypt to hash password and return hashed password
    public String hashPassword(String strPassword) {
        return BCrypt.withDefaults().hashToString(12, strPassword.toCharArray());
    }

    // Uses bcrypt to verify password and return true if password matches hashed password
    public boolean verifyPassword(String strPassword, String strBcryptPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(strPassword.toCharArray(), strBcryptPassword);
        return result.verified;
    }

    // Create dummy users
    public void createUsers() {
        lstUsers = new ArrayList<>();
        lstUsers.add(new User("john@gmail.com", "John Madden", hashPassword("password1")));
        lstUsers.add(new User("gordon@gmail.com", "Gordon Freeman", hashPassword("password2")));
        lstUsers.add(new User("oniell@gmail.com", "Jack O'Neill", hashPassword("password3")));
    }

    public void rememeberFields() {
        // If checkbox is unchecked then clear fields
        if (!chkRemember.isChecked()) {
            etLoginEmail.setText("");
            etLoginPassword.setText("");
        }
    }

    // Save login fields to SharedPreferences
    public void saveLoginFields() {
        // Get email and password from EditText
        String strEmail = etLoginEmail.getText().toString();
        String strPassword = etLoginPassword.getText().toString();

        // Save email and password to SharedPreferences
        SharedPreferences.Editor editor = loginSharedPrefs.edit();
        editor.putString("email", strEmail);
        editor.putString("password", strPassword);
        editor.apply();
    }

    // Load login fields from SharedPreferences
    public void loadLoginFields() {
        // Check if loginSharedPrefs is null
        if (loginSharedPrefs == null)
            return;

        // Get email and password from SharedPreferences
        String strEmail = loginSharedPrefs.getString("email", "");
        String strPassword = loginSharedPrefs.getString("password", "");

        // Check if email and password are empty
        if (strEmail.isEmpty() || strPassword.isEmpty())
            return;

        if (chkRemember.isChecked())
            chkRemember.setChecked(true);

        // Set email and password to EditText
        etLoginEmail.setText(strEmail);
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