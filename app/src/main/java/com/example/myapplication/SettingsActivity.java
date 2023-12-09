package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.net.CookieManager;

public class SettingsActivity extends AppCompatActivity {
    // Navigation sidebar - Hunter
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    SessionManager sessionManager; // SessionManager to store session - Hunter
    CookieManager cookieManager; // CookieManager to store cookies - Hunter

    // Declare a SharedPreferences object
    SharedPreferences sharedPreferences, savedSessionSharedPrefs;
    SharedPreferences.Editor editor;

    // GUI stuff
    TextView tvDefaultAddress;
    Switch swtDarkMode;
    Switch swtNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        savedSessionSharedPrefs = getSharedPreferences("saved_session", MODE_PRIVATE); // Initialize the SharedPreferences - Hunter
        sessionManager = new SessionManager(this); // Initialize the session manager - Hunter
        cookieManager = new CookieManager(); // Initialize the cookie manager - Hunter

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize activity views
        tvDefaultAddress = findViewById(R.id.etDefaultAddress);
        swtDarkMode = findViewById(R.id.swtDarkMode);
        swtNotifications = findViewById(R.id.swtNotifications);

        // Set default values for notifications
        editor.putBoolean("settingsNotifications", true);
        editor.apply();

        // Navigation sidebar - Hunter
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the toolbar as the action bar

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

                Toast.makeText(this, "Dark mode enabled", Toast.LENGTH_SHORT).show();

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
                editor.putBoolean("settingsDarkMode", false);
                editor.apply();
                // Change to light mode
                swtDarkMode.setText("Off");

                Toast.makeText(this, "Dark mode disabled", Toast.LENGTH_SHORT).show();

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // Toast.makeText(this, "Changes will take effect on the next screen.", Toast.LENGTH_SHORT).show();
            // TODONE - Need to figure out how we're implementing dark mode
            // TODO - Need to test if this method of dark mode control is working as intended,
            //      currently lacking navigation to settings page
        });

        swtNotifications.setOnCheckedChangeListener((compoundButton,isChecked) ->{
            if (swtNotifications.isChecked()){
                editor.putBoolean("settingsNotifications", true);
                editor.apply();
                // Change notifications on
                swtNotifications.setText("On");

                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();

            }
            else{
                editor.putBoolean("settingsNotifications", false);
                editor.apply();
                // Change notifications off
                swtNotifications.setText("Off");

                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();

            }

        });

        sideNavigation(); // Set the navigation sidebar - Hunter
    }

    /* ---------------- Hunter - Sidebar navigation ----------------*/
    // Navigation sidebar item click listener
    public void sideNavigation() {
        navigationView.setCheckedItem(R.id.nav_settings); // Set the home item as checked

        // Set the navigation sidebar
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the id of the clicked item

            // Start the corresponding activity
            if (itemId == R.id.nav_home) {
                // Go to home
                switchActivity(new Intent(this, HomeActivity.class));
            }
            else if (itemId == R.id.nav_locations) {
                // Go to locations
                switchActivity(new Intent(this, PandaLocationsActivity.class));
            }
            else if (itemId == R.id.nav_map) {
                // Go to map
                switchActivity(new Intent(this, MapsActivity.class));
            }
            else if (itemId == R.id.nav_signout) {
                // Clear the cookies
                if (cookieManager.getCookieStore().getCookies().size() > 0)
                    cookieManager.getCookieStore().removeAll();

                sessionManager.clearSession(); // Clear the session

                savedSessionSharedPrefs.edit().clear().apply(); // Clear the saved session information

                // Display toast message
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();

                // Handle sign out
                switchActivity(new Intent(this, LoginActivity.class));
            }
            else if (itemId == R.id.nav_settings) {
                // Do nothing
            }

            // Close the navigation drawer
            drawer.closeDrawers();
            return true;
        });
    }

    // Switch activity
    public void switchActivity(Intent intent) {
        if (intent.getComponent().getClassName().contains("LoginActivity"))
            intent.putExtra("signOut", true); // Pass the sign out flag to the next activity"

        startActivity(intent); // Start the activity
        finish(); // Close the current activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the Up button to open the navigation drawer
        drawer.openDrawer(navigationView);
        return true;
    }
}