package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PandaLocationsActivity extends AppCompatActivity {

    ArrayList<Panda> pandas;
    RecyclerView rvPandas;
    RequestQueue queue;
    Geocoder geocoder;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    FusedLocationProviderClient flpCli;
    PandaAdapter pandaAdapter;
    Spinner spFilter;
    String filter_options[] = {"Unsorted", "Rating", "Distance"};
    Button btnSearch;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panda_locations);

        pandas = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        geocoder = new Geocoder(this);
        flpCli = LocationServices.getFusedLocationProviderClient(this);

        spFilter = findViewById(R.id.spFilter);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, filter_options);
        spFilter.setAdapter(filterAdapter);

        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);



//        pandas.add(new Panda("123 Sesame Street on the water", 2, 3, 5.5, 1));
//        pandas.add(new Panda("Red Ball", 5, 8, 12.0, 3));
//        pandas.add(new Panda("Crystal Vase", 10, 15, 20.5, 2));
//        pandas.add(new Panda("Vintage Book", 7, 10, 5.0, 4));
//        pandas.add(new Panda("Silver Necklace", 3, 5, 18.2, 1));
//        pandas.add(new Panda("Wooden Chair", 12, 18, 22.7, 5));
//        pandas.add(new Panda("Blue Painting", 8, 12, 16.5, 2));
//        pandas.add(new Panda("Ceramic Mug", 4, 6, 7.8, 3));
//        pandas.add(new Panda("Plush Teddy Bear", 6, 9, 11.3, 2));
//        pandas.add(new Panda("Glass Bowl", 9, 13, 14.6, 4));
//        pandas.add(new Panda("Metallic Sculpture", 11, 16, 25.0, 3));
        //pandas = getPandasFromServer();
        getPandasFromServer();

        for (int i = 0; i < pandas.size(); i++) {
            Log.d("mainPandas", "id: " + pandas.get(i).id + " lat " + pandas.get(i).lat + " lon " + pandas.get(i).lon);
        }

        rvPandas = findViewById(R.id.rvLocations);

        //PandaAdapter pandaAdapter = new PandaAdapter(this, pandas);
        pandaAdapter = new PandaAdapter(this, pandas);
        rvPandas.setAdapter(pandaAdapter);

        LinearLayoutManager pandaManager = new LinearLayoutManager(this);
        rvPandas.setLayoutManager(pandaManager);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spFilter.getItemAtPosition(i).toString().equalsIgnoreCase("rating")) {
                    Log.d("ratedPandas", "WE IN RATE");
                    ArrayList<Panda> ratedPandas = new ArrayList<>();

                    for (int j = 0; j < pandas.size(); j++) {
                        ratedPandas.add(pandas.get(j));
                    }

                    Collections.sort(ratedPandas, Comparator.comparingDouble(Panda::getRating).reversed());


                    PandaAdapter ratedAdapter = new PandaAdapter(getApplicationContext(), ratedPandas);
                    rvPandas.setAdapter(ratedAdapter);

//                    filter_options = new String[]{"Rating", "Distance"};
//                    ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(getApplicationContext(),
//                            android.R.layout.simple_spinner_dropdown_item, filter_options);
//                    spFilter.setAdapter(filterAdapter);
                }
                else if (spFilter.getItemAtPosition(i).toString().equalsIgnoreCase("distance")) {
                    Log.d("distancedPandas", "WE IN DISTANCE");
                    ArrayList<Panda> distancedPandas = new ArrayList<>();

                    for (int j = 0; j < pandas.size(); j++) {
                        distancedPandas.add(pandas.get(j));
                    }

                    Collections.sort(distancedPandas, Comparator.comparingDouble(Panda::getDistance));

                    PandaAdapter distancedAdapter = new PandaAdapter(getApplicationContext(), distancedPandas);
                    rvPandas.setAdapter(distancedAdapter);

//                    filter_options = new String[]{"Distance", "Rating"};
//                    ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(getApplicationContext(),
//                            android.R.layout.simple_spinner_dropdown_item, filter_options);
//                    spFilter.setAdapter(filterAdapter);
                }
                else if (spFilter.getItemAtPosition(i).toString().equalsIgnoreCase("unsorted")) {
                    rvPandas.setAdapter(pandaAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //ItemTouchHelper helper = new ItemTouchHelper()

        btnSearch.setOnClickListener(e -> {
            if (btnSearch.getText().toString().equalsIgnoreCase("Search")) {
                String search = etSearch.getText().toString();
                search = search.toLowerCase();

                ArrayList<Panda> searchedWords = new ArrayList<>();
                for (int i = 0; i < pandas.size(); i++) {
                    if (pandas.get(i).getAddress().toLowerCase().contains(search)) {
                        searchedWords.add(pandas.get(i));
                    }
                }

                PandaAdapter searchedAdapter = new PandaAdapter(getApplicationContext(), searchedWords);
                rvPandas.setAdapter(searchedAdapter);
                btnSearch.setText("Clear");
            }
            else {
                etSearch.setText("");
                btnSearch.setText("Search");

                rvPandas.setAdapter(pandaAdapter);
            }
        });

    }

    public void getPandasFromServer() {
        //ArrayList<Panda> tempPandas = new ArrayList<>();

        String url = "https://pandaexpress-rating-backend-group5.onrender.com/allPandas";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("getPandasFromServer", "Success!!!");
                        Log.d("getPandasFromServer", response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject responseObj = response.getJSONObject(i);


                                Panda tempPanda = new Panda (
                                        responseObj.getString("_id"),
                                        responseObj.getDouble("latitude"),
                                        responseObj.getDouble("longitude")
                                );

                                double avgRating = 0;
                                if (responseObj.has("averageRating")) {
                                    avgRating = responseObj.getDouble("averageRating");
                                    tempPanda.setRating(avgRating);
                                }
                                else {
                                    tempPanda.setRating(0);
                                }

                                pandas.add(tempPanda);
                                //pandas = tempPandas;
                            }
                            catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        //output all pulled pandas
                        for (int i = 0; i < pandas.size(); i++) {
                            Log.d("getPandasFromServer", "Panda lat: " + pandas.get(i).lat + " lon: " + pandas.get(i).lon + " id " + pandas.get(i).id);
                        }

                        //get address for each panda
                        for (int i = 0; i < pandas.size(); i++) {
                            double curLat = pandas.get(i).getLat();
                            double curLng = pandas.get(i).getLon();
                            String curAddress = convertCoordinatesToAddress(curLat, curLng);

                            pandas.get(i).setAddress(curAddress);
                        }

                        //calcualte distance to each panda
                        convertCoordinatesToDistance();

                        //get rating for each panda


                        pandaAdapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("getPandasFromServer", error.toString());
                    }
                });

        Log.d("outside", "request to string: " + request.toString());
        queue.add(request);
        //return tempPandas;
    }

    public void convertCoordinatesToDistance() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("locationPermission", "Location: permission denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else {
            Log.d("locationPermission", "Location: permission granted");
        }

        flpCli.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("CurrentLocation", location.toString());
                double phoneLat = location.getLatitude();
                double phoneLng = location.getLongitude();

                for (int i = 0; i < pandas.size(); i++) {
                    double pandaLat = pandas.get(i).getLat();
                    double pandaLng = pandas.get(i).getLon();


                    double earthRadius = 3958.8;//radius of globe in miles
                    double dLat = Math.toRadians(pandaLat - phoneLat);
                    double dLon = Math.toRadians(pandaLng - phoneLng);

                    //Haversine formula
                    double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(phoneLat))
                            * Math.cos(Math.toRadians(pandaLat)) * Math.pow(Math.sin(dLon / 2), 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    double distance = earthRadius * c;

                    distance = Math.round(distance * 10.0) / 10;

                    pandas.get(i).setDistance(distance);

                }

                pandaAdapter.notifyDataSetChanged();
            }
        });

    }



    public String convertCoordinatesToAddress(double lat, double lng) {
        String address;
        String[] addressParts;

        try {
            address = geocoder.getFromLocation(lat, lng, 1).get(0).getAddressLine(0);
            addressParts = address.split(",");
            address = addressParts[0] +", " + addressParts[1];
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return address;
    }

}