package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnExtended;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this is my little dummy dev button for temporary
        //instead of making the extended activity the launching activity
        btnExtended = findViewById(R.id.btnExtended);
        btnExtended.setOnClickListener(e -> {
            Intent extended = new Intent(this, ExtendedActivity.class);
            startActivity(extended);
        });
    }
}