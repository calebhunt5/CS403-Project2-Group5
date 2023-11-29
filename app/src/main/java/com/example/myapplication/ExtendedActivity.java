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

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;

public class ExtendedActivity extends AppCompatActivity implements OnMapReadyCallback {
    //elements on page
    TextView tvAddress, tvMilesAway;
    GoogleMap map;
    ProgressBar pbLoader;

    //recycler view and adapter for reviews
    RecyclerView rvReviews;
    ArrayList<PandaReview> arrReviews;
    ReviewAdapter adapter;

    //location stuff
    Geocoder geocoder;
    FusedLocationProviderClient flpCli;
    SupportMapFragment mapFragment;
    public static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended);

        // TODO
        // * Get location from data passed in from locations list
        // * Get users current position
        // * Method to calculate miles away

        //get location permission granted
        //in the full app, this will probably be handled by the launcher
        //then i'll take this out
        askLocationPermission();

        //get elements
        tvAddress = findViewById(R.id.tvAdddress);
        tvMilesAway = findViewById(R.id.tvMilesAway);
        rvReviews = findViewById(R.id.rvReviews);
        //this loader is initially visible while everything else is invisible
        pbLoader = findViewById(R.id.pbLoader);

        //setup fragment for map, geocoder, and flp client
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        flpCli = LocationServices.getFusedLocationProviderClient(this);

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

    public void askLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d("LocationPermission", "Location: permissions granted");
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //in the future this will get the location the user clicked on
        //for now it is the svsu panda express
        map = googleMap;

        //let latitude and longitude, make marker and show
        LatLng svsuPanda = new LatLng(43.513905262373456, -83.96109288094384);
        Marker m = map.addMarker(new MarkerOptions().position(svsuPanda));
        map.moveCamera(CameraUpdateFactory.newLatLng(svsuPanda));

        //get address and update text for address
        Address address = null;
        try {
            address = geocoder.getFromLocation(svsuPanda.latitude, svsuPanda.longitude, 1).get(0);
            tvAddress.setText(address.getAddressLine(0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //get distance from current users location to
        //the selected panda express
        getMilesAway(svsuPanda);
    }

    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        //calculate the distance between the two points
        //based on longitude and latitude
        double theta = lng1 - lng2;
        double dist = Math.sin(degreeToRadian(lat1))
                * Math.sin(degreeToRadian(lat2))
                + Math.cos(degreeToRadian(lat1))
                * Math.cos(degreeToRadian(lat2))
                * Math.cos(degreeToRadian(theta));
        dist = Math.acos(dist);
        dist = radianToDegree(dist);
        //convert to miles
        dist = dist * 60 * 1.1515;
        return dist;
    }

    public double degreeToRadian(double degree) {
        //utility function to convert degree to radian
        return (degree * Math.PI / 180.0);
    }

    public double radianToDegree(double radian) {
        //utility function to convert radian to degree
        return (radian * 180.0 / Math.PI);
    }

    public void getMilesAway(LatLng panda) {
        Log.d("milesAway", "inMilesAwayMethod");
        //check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            Log.d("LocationPermission", "Location: permissions denied");
        }
        else {
            Log.d("LocationPermission", "Location: permissions granted");
        }

        //if successful at getting current location, calculate distance and display
        flpCli.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(new OnSuccessListener<Location>() {
            public void onSuccess(Location location) {
                Log.d("CurrentLocation", location.toString());
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                double milesAway = calculateDistance(current.latitude, current.longitude,
                        panda.latitude, panda.longitude);

                //use string.format to create our string
                //so we can format milesAway down to one decimal point
                String milesAwayText = String.format("%.1f miles away", milesAway);

                //set miles away text
                tvMilesAway.setText(milesAwayText);

                //at this point we can assume all the data was loaded
                //i'll probably have to change this later once i implement
                //putting actually data in. anyway
                //call a method to make all the elements that werent visible before visible
                makeEverythingVisible();
            }
        });

        flpCli.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {
                Log.d("CurrentLocation", e.toString());
            }
        });
    }

    public void makeEverythingVisible() {
        //make elements which was initially invisible visible
        tvAddress.setVisibility(View.VISIBLE);
        tvMilesAway.setVisibility(View.VISIBLE);
        rvReviews.setVisibility(View.VISIBLE);

        //make loader invisible
        pbLoader.setVisibility(View.INVISIBLE);
    }
}