// Hunter Wright - Login Page
package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tilLoginEmail, tilLoginPassword;
    EditText etLoginEmail, etLoginPassword;
    CheckBox chkRemember;
    Button btnLogin, btnRegister;

    boolean blnEmail = false, blnPassword = false;

    // Store list of dummy users of class User
    List<User> lstUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get references to EditText, CheckBox, and Buttons
        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        chkRemember = findViewById(R.id.chkRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Create dummy users
        createUsers();

        emailTextChanged();
        passwordChanged();

        btnLogin.setOnClickListener(v -> {
            login();
        });

        btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });
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
                if (s.toString().contains("@") && s.toString().contains(".")) {
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
                if (s.toString().length() >= 8) {
                    blnPassword = true;
                    tilLoginPassword.setError(null);
                }
                else {
                    blnPassword = false;
                    tilLoginPassword.setError("Password must be at least 8 characters");
                }

                // Enable login button if both email and password are valid
                if (blnEmail && blnPassword) {
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }
        });
    }

    public void login() {
        // Get email and password from EditText
        String strEmail = etLoginEmail.getText().toString();
        String strPassword = etLoginPassword.getText().toString();

        boolean blnNotFound = false;

        // Check if email and password are valid
        if (blnEmail && blnPassword) {
            // Check if email and password match a user
            for (User user : lstUsers) {
                if (user.getStrEmail().equals(strEmail) && user.getStrPassword().equals(strPassword)) {
                    blnNotFound = false;

                    // Login successful
                    Intent i = new Intent(LoginActivity.this, LaunchActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                    blnNotFound = true;
            }
        }

        if (blnNotFound)
            Toast.makeText(this, "Email or password is incorrect", Toast.LENGTH_LONG).show();
    }

    // Create dummy users
    public void createUsers() {
        lstUsers = new ArrayList<>();
        lstUsers.add(new User("john@gmail.com", "John Madden", "password1"));
        lstUsers.add(new User("gordon@gmail.com", "Gordon Freeman", "password2"));
        lstUsers.add(new User("oniell@gmail.com", "Jack O'Neill", "password3"));
    }

}