package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
    ArrayList<PandaLocation> pandaLocations;

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


        //add a marker on the map for each panda express location
        //and tie the object to that marker with the tag
        for (int i = 0; i < pandaLocations.size(); i++) {
            PandaLocation currentPanda = pandaLocations.get(i);

            Marker m = map.addMarker(new MarkerOptions().position(
                new LatLng(currentPanda.lat, currentPanda.lng)));
            m.setTag(currentPanda);
        }

        //in the future, this will automatically go to whatever the default
        //location the user set in settings is. for now, it goes to the svsu location
        LatLng svsuPanda = new LatLng(43.513905262373456, -83.96109288094384);
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(svsuPanda).zoom(17).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void getPandaExpressLocations() {
        //url to the server to get all panda express locations
        String url = "https://pandaexpress-rating-backend-group5.onrender.com/allPandas";

        //request object - gets all panda express locations in michigan
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
        new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    // creating a new json object and
                    // getting each object from our json array.
                    try {
                        // we are getting each json object.
                        JSONObject responseObj = response.getJSONObject(i);

                        //make sure average rating actually exists
                        double averageRating = 0.0;
                        if (responseObj.has("averageRating")) {
                            averageRating = responseObj.getDouble("averageRating");
                        }

                        //make a pandaLocation object and store it in an arraylist
                        PandaLocation currentPanda = new PandaLocation(
                                responseObj.getInt("store_id"),
                                averageRating,
                                responseObj.getDouble("latitude"),
                                responseObj.getDouble("longitude"));

                        pandaLocations.add(currentPanda);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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

        queue.add(jsonArrayRequest);
    };

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
            RatingBar ratingBar = v.findViewById(R.id.ratingBar);

            //get current panda express
            PandaLocation currentPanda = (PandaLocation) marker.getTag();

            try {
                //set address in info window
                Address address = null;
                address = geocoder.getFromLocation(marker.getPosition().latitude,
                        marker.getPosition().longitude, 1).get(0);
                tvAddress.setText(address.getAddressLine(0));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //set rating bar to value of average review
            ratingBar.setRating((float) currentPanda.averageRating);

            //onclick listener that takes the user to the extended view when clicked
            //passes in the store number
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {
                    Intent extended = new Intent(v.getContext(), ExtendedActivity.class);
                    extended.putExtra("storeID", currentPanda.storeID);
                    startActivity(extended);
                }
            });

            return v;
        }
    }
}