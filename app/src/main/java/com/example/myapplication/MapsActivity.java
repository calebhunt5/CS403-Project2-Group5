package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //in the future i'm going to have a loop to put all the panda express locations
        //we have in our database as markers on the map. for now, i am just adding
        //the svsu panda express location
        LatLng svsuPanda = new LatLng(43.513905262373456, -83.96109288094384);
        Marker m = map.addMarker(new MarkerOptions().position(svsuPanda));

        //in the future, this will automatically go to whatever the default
        //location the user set in settings is. for now, it goes to the svsu location
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(svsuPanda).zoom(17).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //class for the custom map window
    class PersonInfoWindow implements GoogleMap.InfoWindowAdapter{

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
            TextView ivStars = v.findViewById(R.id.ivStars);

            /* TODO
                * Make info window use some sort of PandaLocation class based on database
                * Make it show info window
                * Make info window go to extended view when clicked
             */
            return v;
        }
    }
}