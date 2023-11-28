package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    // Declare a SharedPreferences object
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // GUI stuff
    TextView tvDefaultAddress;
    Switch swtDarkMode;
    Switch swtNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize activity views
        tvDefaultAddress = findViewById(R.id.etDefaultAddress);
        swtDarkMode = findViewById(R.id.swtDarkMode);
        swtNotifications = findViewById(R.id.swtNotifications);

        // Set stored preferences
        if (sharedPreferences.getBoolean("settingsDarkMode",false)) {
            swtDarkMode.setChecked(true);
            swtDarkMode.setText("On");
        }
        else{
            swtDarkMode.setChecked(false);
            swtDarkMode.setText("Off");
        }

        if (sharedPreferences.getBoolean("settingsNotifications",false)) {
            swtNotifications.setChecked(true);
            swtNotifications.setText("On");
        }
        else{
            swtNotifications.setChecked(false);
            swtNotifications.setText("Off");
        }

        // TODO - Populate address
    }
}