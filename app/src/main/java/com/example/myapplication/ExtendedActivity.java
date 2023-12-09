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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ExtendedActivity extends AppCompatActivity implements OnMapReadyCallback {
    //elements on page
    TextView tvAddress, tvMilesAway;
    Button btnAdd;
    GoogleMap map;
    ProgressBar pbLoader;
    PandaLocation pandaLocation;
    int storeID;

    //recycler view and adapter for reviews
    RecyclerView rvReviews;
    ArrayList<PandaReview> arrReviews;
    ReviewAdapter adapter;

    //location stuff
    Geocoder geocoder;
    FusedLocationProviderClient flpCli;
    SupportMapFragment mapFragment;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended);

        //get elements
        tvAddress = findViewById(R.id.tvAdddress);
        tvMilesAway = findViewById(R.id.tvMilesAway);
        rvReviews = findViewById(R.id.rvReviews);
        btnAdd = findViewById(R.id.btnAdd);
        //this loader is initially visible while everything else is invisible
        pbLoader = findViewById(R.id.pbLoader);

        //setup fragment for map, geocoder, and flp client
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        flpCli = LocationServices.getFusedLocationProviderClient(this);

        //set arraylist for reviews, adapter, and layout manager
        arrReviews = new ArrayList<>();
        adapter = new ReviewAdapter(this, arrReviews);
        rvReviews.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvReviews.setLayoutManager(manager);

        //get ready to make request
        queue = Volley.newRequestQueue(this.getApplicationContext());
        getUserID();
        storeID = getIntent().getIntExtra("storeID", 0);

        //on click method to go to the add review page
        //passes in the store id
        btnAdd.setOnClickListener(e -> {
            Intent addIntent = new Intent(this, AddReviewActivity.class);
            addIntent.putExtra("storeID", storeID);
            startActivity(addIntent);
        });
    }

    public void getPandaStoreData(int storeID) {
        //url to the server to get all panda express locations
        String url = "https://pandaexpress-rating-backend-group5.onrender.com/pandas/"+storeID;

        //request object - gets all the specific panda express location that was selected
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObj) {
                try {
                    //make sure average rating actually exists
                    double averageRating = 0.0;
                    if (responseObj.has("averageRating")) {
                        averageRating = responseObj.getDouble("averageRating");
                    }

                    //make object we can use
                    pandaLocation = new PandaLocation(
                            responseObj.getInt("store_id"),
                            averageRating,
                            responseObj.getDouble("latitude"),
                            responseObj.getDouble("longitude"));

                    //set up arraylist of reviews from json response
                    JSONArray reviewsJsonArr = responseObj.getJSONArray("ratings");
                    for (int i = 0; i < reviewsJsonArr.length(); i++) {
                        JSONObject currentReviewObj = reviewsJsonArr.getJSONObject(i);
                        PandaReview currentReview = new PandaReview(
                                currentReviewObj.getString("actualUserName"),
                                currentReviewObj.getString("description"),
                                currentReviewObj.getInt("rating"),
                                currentReviewObj.getInt("numLikes"),
                                false,
                                currentReviewObj.getString("_id")
                        );
                        arrReviews.add(currentReview);
                    }
                    //tell adapter to update recycler view
                    adapter.notifyDataSetChanged();

                    //make marker and show
                    LatLng currentPanda = new LatLng(pandaLocation.lat, pandaLocation.lng);
                    Marker m = map.addMarker(new MarkerOptions().position(currentPanda));
                    map.moveCamera(CameraUpdateFactory.newLatLng(currentPanda));

                    //get address and update text for address
                    Address address = null;
                    try {
                        address = geocoder.getFromLocation(
                                pandaLocation.lat, pandaLocation.lng, 1).get(0);
                        tvAddress.setText(address.getAddressLine(0));

                        //get distance from current users location to
                        //the selected panda express
                        getMilesAway(currentPanda);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse", error.toString());
                throw new RuntimeException(error);
            }
        });

        queue.add(jsonObjectRequest);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        //get store data for selected panda express
        if (storeID != 0) {
            getPandaStoreData(storeID);
        }
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

    public void getUserID() {
        //url to the server to get all panda express locations
        String url = "https://pandaexpress-rating-backend-group5.onrender.com/users/me";

        //request object - gets all the specific panda express location that was selected
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObj) {
                        try {
                            Log.d("myuser", responseObj.toString());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onErrorResponse", error.toString());
                        throw new RuntimeException(error);
                    }
                });

        queue.add(jsonObjectRequest);
    }

    public void makeEverythingVisible() {
        //make elements which was initially invisible visible
        tvAddress.setVisibility(View.VISIBLE);
        tvMilesAway.setVisibility(View.VISIBLE);
        rvReviews.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);

        //make loader invisible
        pbLoader.setVisibility(View.INVISIBLE);
    }
}