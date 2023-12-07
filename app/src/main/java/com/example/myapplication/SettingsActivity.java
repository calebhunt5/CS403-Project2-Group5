package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

        // Initialize settings in shared preferences
        if (!sharedPreferences.contains("settingsDarkMode")){
            editor.putBoolean("settingsDarkMode", false);
            editor.apply();
        }
        if (!sharedPreferences.contains("settingsNotifications")){
            editor.putBoolean("settingsNotifications", false);
            editor.apply();
        }

        // Set stored preferences
        if (sharedPreferences.contains("settingsDefaultAddress")){
            tvDefaultAddress.setText(sharedPreferences.getString("settingsDefaultAddress",""));
        }

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

        // Toggle listeners
        swtDarkMode.setOnCheckedChangeListener((compoundButton,isChecked) ->{
            if (swtDarkMode.isChecked()){
                editor.putBoolean("settingsDarkMode", true);
                editor.apply();
                // Change to dark mode
                swtDarkMode.setText("On");

            }
            else{
                editor.putBoolean("settingsDarkMode", false);
                editor.apply();
                // Change to light mode
                swtDarkMode.setText("Off");

            }
            Toast.makeText(this, "Changes will take effect on the next screen.", Toast.LENGTH_SHORT).show();
            // TODO - Need to figure out how we're implementing dark mode
        });

        swtNotifications.setOnCheckedChangeListener((compoundButton,isChecked) ->{
            if (swtNotifications.isChecked()){
                editor.putBoolean("settingsNotifications", true);
                editor.apply();
                // Change notifications on
                swtNotifications.setText("On");

            }
            else{
                editor.putBoolean("settingsNotifications", false);
                editor.apply();
                // Change notifications off
                swtNotifications.setText("Off");

            }

        });
    }
}