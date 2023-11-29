/* Danielle Smith
    11/29/23
    Extended view code
    Allows the user to see and interact with all the reviews
    for one specific Panda Express location
 */

package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.Manifest;

import com.google.android.gms.maps.MapView;

import java.io.IOException;
import java.util.ArrayList;

public class ExtendedActivity extends AppCompatActivity {
    //elements and adapter
    TextView tvAddress, tvMilesAway;
    MapView mvMap;
    RecyclerView rvReviews;
    ArrayList<PandaReview> arrReviews;
    ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended);

        // TODO
        // * Get location from data passed in from locations list
        // * Get users current position
        // * Method to calculate miles away

        //get elements
        tvAddress = findViewById(R.id.tvAdddress);
        tvMilesAway = findViewById(R.id.tvMilesAway);
        mvMap = findViewById(R.id.mvMap);
        rvReviews = findViewById(R.id.rvReviews);

        arrReviews = new ArrayList<>();
        //dummy reviews for development
        PandaReview review1 = new PandaReview("Daniel Jackson", 4, 2, false);
        PandaReview review2 = new PandaReview("Samantha Carter", 3, 3, false);
        arrReviews.add(review1);
        arrReviews.add(review2);

        //set adapter and layout manager
        adapter = new ReviewAdapter(this, arrReviews);
        rvReviews.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvReviews.setLayoutManager(manager);
    }
}