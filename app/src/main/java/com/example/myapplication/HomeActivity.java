package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "notifyTag";
    SessionManager sessionManager; // SessionManager to store session
    final String strLocationsURL = "https://pandaexpress-rating-backend-group5.onrender.com/allPandas";

    SharedPreferences savedSessionSharedPrefs; // SharedPreferences to store saved session information


    // Navigation sidebar
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    AutoCompleteTextView atxtSearchAddress; // Search address text view
    Button btnSearch; // Search button

    SessionManager sessionManager; // SessionManager to store session
    CookieManager cookieManager; // CookieManager to store cookies

    List<String> arrPandaLocations = new ArrayList<>(); // List of Panda locations from the backend server

    String strID = ""; // User ID

    public static final int NOTIFICATION_REQ_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        savedSessionSharedPrefs = getSharedPreferences("saved_session", MODE_PRIVATE); // Initialize the SharedPreferences

        sessionManager = new SessionManager(this); // Initialize the session manager

        cookieManager = new CookieManager(); // Initialize the cookie manager

        if (savedSessionSharedPrefs.contains("sessionToken")) {
            // Get the session token
            String strSessionToken = savedSessionSharedPrefs.getString("sessionToken", null);

            // Check if the session token is null
            if (strSessionToken == null) {
                // Display toast message
                Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();

                // Handle sign out
                switchActivity(new Intent(this, LoginActivity.class));
            }
            else {
                // Save the session token
                sessionManager.saveSessionInformation(strSessionToken);
            }
        }
        else {
            // Display toast message
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();

            // Handle sign out
            switchActivity(new Intent(this, LoginActivity.class));
        }

        // Check if location permissions are granted
        checkLocationPermissions();

        getViews(); // Bind views

        addLocations(); // Add locations to the list

        getID(); // Get ID from shared preferences

        // Search address text view click listener
        searchAddress();

        sideNavigation(); // Navigation sidebar item click listener



        checkForNotificationPermissions();
        //notificationService = new NotificationService();

        Intent i = new Intent(this, NotificationService.class);
        //ContextCompat.startForegroundService(this, i);
        startService(i);


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

    public void searchAddress() {
        // Go to list activity
        btnSearch.setOnClickListener(v -> {
            String strAddress = atxtSearchAddress.getText().toString(); // Get the address

            // Check if the address is empty
            if (strAddress.isEmpty() || strAddress.equals(" "))
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            else {
                // Go to list activity
                Intent intent = new Intent(this, PandaLocationsActivity.class);
                intent.putExtra("address", strAddress);
                startActivity(intent);
            }
        });
    }

    // Add locations to the list
    public void addLocations() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("Volley", "Requesting locations...");

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, strLocationsURL, null,
            response -> {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String strAddress = jsonObject.getString("address");
                        arrPandaLocations.add(strAddress);
                    }

                    // Set the adapter for the search address text view
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrPandaLocations);
                    atxtSearchAddress.setAdapter(adapter);

                    Log.d("Volley", "Locations requested");
                } catch (JSONException e) {
                    Log.d("Volley", e.toString());
                    e.printStackTrace();
                }
            },
            error -> Log.d("Volley", error.toString())
        );

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    public void getID() {
        // Get ID from shared preferences
        strID = savedSessionSharedPrefs.getString("user_id", null);
    }

    // Navigation sidebar item click listener
    public void sideNavigation() {
        navigationView.setCheckedItem(R.id.nav_home); // Set the home item as checked

        // Set the navigation sidebar
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the id of the clicked item

            // Start the corresponding activity
            if (itemId == R.id.nav_home) {
                // Do nothing
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
                // Go to settings
                switchActivity(new Intent(this, SettingsActivity.class));
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

    // Bind views
    public void getViews() {
        atxtSearchAddress = findViewById(R.id.atxtSearchAddress); // Search address text view
        btnSearch = findViewById(R.id.btnSearch); // Search button

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