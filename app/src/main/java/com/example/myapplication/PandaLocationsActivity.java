package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PandaLocationsActivity extends AppCompatActivity {

    // Navigation sidebar
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    SharedPreferences savedSessionSharedPrefs; // SharedPreferences to store saved session information

    SessionManager sessionManager; // SessionManager to store session
    CookieManager cookieManager; // CookieManager to store cookies

    ArrayList<Panda> pandas;
    //Creation of variables to display and populate recycler view
    RecyclerView rvPandas;
    RequestQueue queue;
    Geocoder geocoder;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    FusedLocationProviderClient flpCli;
    PandaAdapter pandaAdapter;
    Spinner spFilter;
    String filter_options[] = {"Unsorted", "Rating", "Distance"};

    //creation of search button and edit text
    Button btnSearch;
    EditText etSearch;

    String strID = ""; // User ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panda_locations);

        // Navigation sidebar - Hunter
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the toolbar as the action bar
        savedSessionSharedPrefs = getSharedPreferences("saved_session", MODE_PRIVATE);

        sessionManager = new SessionManager(this); // Initialize the session manager

        cookieManager = new CookieManager(); // Initialize the cookie manager

        pandas = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        geocoder = new Geocoder(this);
        flpCli = LocationServices.getFusedLocationProviderClient(this);

        //create adapter for spinner
        spFilter = findViewById(R.id.spFilter);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, filter_options);
        spFilter.setAdapter(filterAdapter);

        //connect search fields to screen
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);


        //test data
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

        //get pandas from server and enter them into pandas arrayList
        getPandasFromServer();

        //output all pandas from server for testing
//        for (int i = 0; i < pandas.size(); i++) {
//            Log.d("mainPandas", "id: " + pandas.get(i).id + " lat " + pandas.get(i).lat + " lon " + pandas.get(i).lon);
//        }

        //connect recycler view to screen
        rvPandas = findViewById(R.id.rvLocations);

        //connect pandas arraylist to recycler view
        pandaAdapter = new PandaAdapter(this, pandas);
        rvPandas.setAdapter(pandaAdapter);

        //create and establish layout manager for recycler view
        LinearLayoutManager pandaManager = new LinearLayoutManager(this);
        rvPandas.setLayoutManager(pandaManager);

        //when user selects an item from the spinner filter recycler view accordingly
        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //when rating is selected sort pandas from highest to lowest review
                if (spFilter.getItemAtPosition(i).toString().equalsIgnoreCase("rating")) {
                    Log.d("ratedPandas", "WE IN RATE");
                    ArrayList<Panda> ratedPandas = new ArrayList<>();

                    for (int j = 0; j < pandas.size(); j++) {
                        ratedPandas.add(pandas.get(j));
                    }

                    //sort pandas from highest to lowest review
                    Collections.sort(ratedPandas, Comparator.comparingDouble(Panda::getRating).reversed());

                    //change adapter to show pandas in rated order
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

                    //sort list of pandas by distance
                    Collections.sort(distancedPandas, Comparator.comparingDouble(Panda::getDistance));

                    //change adapter to be sorted by distance from close to far
                    PandaAdapter distancedAdapter = new PandaAdapter(getApplicationContext(), distancedPandas);
                    rvPandas.setAdapter(distancedAdapter);

//                    filter_options = new String[]{"Distance", "Rating"};
//                    ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(getApplicationContext(),
//                            android.R.layout.simple_spinner_dropdown_item, filter_options);
//                    spFilter.setAdapter(filterAdapter);
                }
                else if (spFilter.getItemAtPosition(i).toString().equalsIgnoreCase("unsorted")) {
                    //change adapter to be unsorted
                    rvPandas.setAdapter(pandaAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getID(); // Get ID from intent

        // Get address extras from the intent - Hunter
        Intent intent = getIntent();
        String strAddress = intent.getStringExtra("address");
        if (strAddress != null)
            etSearch.setText(strAddress);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d("test", "SWIPE REGISTER");

                int position = viewHolder.getAdapterPosition();
                Panda selectedPanda = pandas.get(position);
                Intent i = new Intent(getApplicationContext(), ExtendedActivity.class);
                i.putExtra("storeID", selectedPanda.storeID);

                Log.d("test", "id: " + selectedPanda.id);
                Log.d("test", "storeID: " + selectedPanda.storeID);

                startActivity(i);

                // Reset adapter
                rvPandas.setAdapter(pandaAdapter);
            }
        });

        helper.attachToRecyclerView(rvPandas);


        //change adapter to display search results when searched for
        btnSearch.setOnClickListener(e -> {
            searchAddress();
        });

        sideNavigation(); // Navigation sidebar item click listener
    }







    // Search address text view click listener - Hunter (Copied from btnSearch and put in its own method)
    public void searchAddress() {
        if (btnSearch.getText().toString().equalsIgnoreCase("Search")) {
            String search = etSearch.getText().toString().trim().replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " "); // Added regex - hunter
            search = search.toLowerCase();

            ArrayList<Panda> searchedWords = new ArrayList<>();
            for (int i = 0; i < pandas.size(); i++) {
                if (pandas.get(i).getFullAddress().toLowerCase().trim().replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ").contains(search)) {
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
    }

    public void getPandasFromServer() {
        //ArrayList<Panda> tempPandas = new ArrayList<>();

        //url for our backend
        String url = "https://pandaexpress-rating-backend-group5.onrender.com/allPandas";

        //get all pandas from backend
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("getPandasFromServer", "Success!!!");
                        Log.d("getPandasFromServer", response.toString());

                        //loop through response and parse out pandas
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject responseObj = response.getJSONObject(i);


                                Panda tempPanda = new Panda (
                                        responseObj.getString("_id"),
                                        responseObj.getDouble("latitude"),
                                        responseObj.getDouble("longitude")
                                );

                                tempPanda.storeID = responseObj.getInt("store_id");

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

                            try {
                                pandas.get(i).setFullAddress(response.getJSONObject(i).getString("address"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
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

        //get permission to use location from user
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

                //calculate distance from phone to each panda location and store in panda object
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

        //try to convert the coordinates into an address, then take only the street address and city
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

    // Navigation sidebar item click listener - Hunter
    public void sideNavigation() {
        navigationView.setCheckedItem(R.id.nav_locations); // Set the locations item as checked

        // Set the navigation sidebar
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the id of the clicked item

            // Start the corresponding activity
            if (itemId == R.id.nav_home) {
                // Go to home
                switchActivity(new Intent(this, HomeActivity.class));
            }
            else if (itemId == R.id.nav_locations) {
                // Do nothing
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

    public void getID() {
        // Get ID from shared preferences
        strID = savedSessionSharedPrefs.getString("user_id", null);
    }

    // Switch activity - Hunter
    public void switchActivity(Intent intent) {
        if (intent.getComponent().getClassName().contains("LoginActivity"))
            intent.putExtra("signOut", true); // Pass the sign out flag to the next activity"

        startActivity(intent); // Start the activity
        finish(); // Close the current activity
    }

    // Hamburger menu - Hunter
    @Override
    public boolean onSupportNavigateUp() {
        // Handle the Up button to open the navigation drawer
        drawer.openDrawer(navigationView);
        return true;
    }
}