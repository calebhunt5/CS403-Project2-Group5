package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.net.CookieHandler;
import java.net.CookieManager;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "notifyTag";
    SessionManager sessionManager; // SessionManager to store session

    // Navigation sidebar
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    CookieManager cookieManager; // CookieManager to store cookies
    SearchView svAddressSearch; // Search bar

    public static final int NOTIFICATION_REQ_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this); // Initialize the session manager

        cookieManager = new CookieManager(); // Initialize the cookie manager

        // Check if location permissions are granted
        checkLocationPermissions();

        getViews(); // Bind views

        sideNavigation(); // Navigation sidebar item click listener



        checkForNotificationPermissions();
        //notificationService = new NotificationService();

        Intent i = new Intent(this, NotificationService.class);
        ContextCompat.startForegroundService(this, i);


        //notificationService.createNotificationChannel();

    }

    //ask for notif permission
    public void checkForNotificationPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQ_CODE);

            Log.d(TAG, "NOTIFY permission deny");

        }
        else {
            Log.d(TAG, "NOTIFY permission granted");
        }
    }

    // Navigation sidebar item click listener
    public void sideNavigation() {
        navigationView.setCheckedItem(R.id.nav_home); // Set the home item as checked

        // Set the navigation sidebar
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the id of the clicked item

            // Start the corresponding activity
            if (itemId == R.id.nav_home) {
                // Do nothing or handle as needed
            } else if (itemId == R.id.nav_signout) {
                // Clear the cookies
                if (cookieManager.getCookieStore().getCookies().size() > 0)
                    cookieManager.getCookieStore().removeAll();

                sessionManager.clearSession(); // Clear the session

                // Display toast message
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();

                // Handle sign out
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish(); // Close the current activity
            }

            // Close the navigation drawer
            drawer.closeDrawers();
            return true;
        });
    }

    public void getViews() {
        svAddressSearch = findViewById(R.id.svAddressSearch); // Search bar

        // Navigation sidebar
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the toolbar as the action bar
    }

    // Check location permissions
    public void checkLocationPermissions() {
        // Check if location permissions are granted
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED)
            // Request location permissions
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the Up button to open the navigation drawer
        drawer.openDrawer(navigationView);
        return true;
    }
}