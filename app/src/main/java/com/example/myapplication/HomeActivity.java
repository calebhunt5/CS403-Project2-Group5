package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    // Navigation sidebar
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    SearchView svAddressSearch; // Search bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getViews(); // Bind views

        sideNavigation(); // Navigation sidebar item click listener
    }

    // Navigation sidebar item click listener
    public void sideNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the id of the clicked item

            // Start the corresponding activity
            if (itemId == R.id.nav_home) {
                // Do nothing or handle as needed
            } else if (itemId == R.id.nav_signout) {
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

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the Up button to open the navigation drawer
        drawer.openDrawer(navigationView);
        return true;
    }
}