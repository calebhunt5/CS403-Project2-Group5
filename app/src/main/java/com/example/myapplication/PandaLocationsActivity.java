package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class PandaLocationsActivity extends AppCompatActivity {

    ArrayList<Panda> pandas;
    RecyclerView rvPandas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panda_locations);

        pandas = new ArrayList<>();

        pandas.add(new Panda("123 Sesame Street on the water", 2, 3, 5.5, 1));
        pandas.add(new Panda("Red Ball", 5, 8, 12.0, 3));
        pandas.add(new Panda("Crystal Vase", 10, 15, 20.5, 2));
        pandas.add(new Panda("Vintage Book", 7, 10, 5.0, 4));
        pandas.add(new Panda("Silver Necklace", 3, 5, 18.2, 1));
        pandas.add(new Panda("Wooden Chair", 12, 18, 22.7, 5));
        pandas.add(new Panda("Blue Painting", 8, 12, 16.5, 2));
        pandas.add(new Panda("Ceramic Mug", 4, 6, 7.8, 3));
        pandas.add(new Panda("Plush Teddy Bear", 6, 9, 11.3, 2));
        pandas.add(new Panda("Glass Bowl", 9, 13, 14.6, 4));
        pandas.add(new Panda("Metallic Sculpture", 11, 16, 25.0, 3));

        rvPandas = findViewById(R.id.rvLocations);

        PandaAdapter pandaAdapter = new PandaAdapter(this, pandas);
        rvPandas.setAdapter(pandaAdapter);

        LinearLayoutManager pandaManager = new LinearLayoutManager(this);
        rvPandas.setLayoutManager(pandaManager);

        //ItemTouchHelper helper = new ItemTouchHelper()

    }
}