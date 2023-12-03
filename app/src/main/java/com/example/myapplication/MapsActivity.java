package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private ActivityMapsBinding binding;
    Geocoder geocoder;
    RequestQueue queue;
    ArrayList<LatLng> pandaLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get all panda express locations
        pandaLocations = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        getPandaExpressLocations();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(new PandaInfoWindow());

        Log.d("pandaExpress-map", "mapready");
        //add a marker on the map for each panda express location
        for (int i = 0; i < pandaLocations.size(); i++) {
            LatLng currentPandaLocation = pandaLocations.get(0);

            LatLng pandaLocation = new LatLng(currentPandaLocation.latitude, currentPandaLocation.longitude);
            Marker m = map.addMarker(new MarkerOptions().position(pandaLocation));
        }

        //in the future, this will automatically go to whatever the default
        //location the user set in settings is. for now, it goes to the svsu location
        LatLng svsuPanda = new LatLng(43.513905262373456, -83.96109288094384);
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(svsuPanda).zoom(17).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void getPandaExpressLocations() {
        Log.d("getPandaExpressLocations", "hit method");

        String url = "https://pandaexpress-rating-backend-group5.onrender.com/allPandas";
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("getPandaExpressLocations",response.toString());

                    try {
                        //get all the locations
                        JSONArray locations = response.getJSONArray("results");
                        for (int i = 0; i < locations.length(); i++) {
                            //get latitude and longitude
                            double currentLat = locations.getJSONObject(i).getDouble("latitude");
                            double currentLng = locations.getJSONObject(i).getDouble("latitude");

                            //for each location, make a marker from the latitude and longitude
                            LatLng pandaLocation = new LatLng(currentLat, currentLng);

                            //add this marker to arraylist of markers
                            pandaLocations.add(pandaLocation);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.d("getPandaExpressLocations", error.toString()));
        queue.add(r);
    };

    public void markPandaExpressLocations() {
        Log.d("markPandaExpressLocations", "hit method");


    }

    //class for the custom map window
    class PandaInfoWindow implements GoogleMap.InfoWindowAdapter{

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            View v = LayoutInflater.from(MapsActivity.this).inflate(R.layout.layout_panda_window,null);
            TextView tvAddress = v.findViewById(R.id.tvAddress);
            ImageView ivStars = v.findViewById(R.id.ivStars);

            //get address and update text for address
            Address address = null;
            try {
                address = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1).get(0);
                tvAddress.setText(address.getAddressLine(0));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //set stars image based on average
            //no data right now however
            int average = 0;
            switch (average) {
                case 1:
                    ivStars.setImageResource(R.drawable.one_star);
                    break;
                case 2:
                    ivStars.setImageResource(R.drawable.two_stars);
                    break;
                case 3:
                    ivStars.setImageResource(R.drawable.three_stars);
                    break;
                case 4:
                    ivStars.setImageResource(R.drawable.four_stars);
                    break;
                case 5:
                    ivStars.setImageResource(R.drawable.five_stars);
                    break;
                default:
                    ivStars.setImageResource(R.drawable.zero_stars);
                    break;
            }


            /* TODO
                * Make info window use some sort of PandaLocation class based on database
                * Make it show info window
                * Make info window go to extended view when clicked
                * Make info window show appropriate image based on average stars
             */
            return v;
        }
    }
}