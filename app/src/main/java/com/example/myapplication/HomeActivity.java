package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    // Navigation sidebar
    AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    // Search bar
    SearchView svAddressSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        svAddressSearch = findViewById(R.id.svAddressSearch);

//        drawer = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_locations, R.id.nav_map, R.id.nav_signout,R.id.nav_settings)
//                .setOpenableLayout(drawer)
//                .build();
//
//        // Set up navigation sidebar
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}