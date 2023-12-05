package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnExtended, btnMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //these are my little buttons for temporary development tings
        btnMaps = findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(e -> {
            Intent extended = new Intent(this, MapsActivity.class);
            startActivity(extended);
        });
    }
}