package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AddReviewActivity extends AppCompatActivity {
    String userID;
    RequestQueue queue;
    Button btnSubmit;
    EditText etReview;
    SeekBar sbRating;
    RatingBar rbRating;
    int storeID;
    ProgressBar pbAdd;
    SharedPreferences userSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        //get elements on page
        pbAdd = findViewById(R.id.pbAdd);
        etReview = findViewById(R.id.etReview);
        sbRating = findViewById(R.id.sbRating);
        rbRating = findViewById(R.id.rbRating);
        btnSubmit = findViewById(R.id.btnSubmit);

        userSharedPref = getSharedPreferences("saved_session", MODE_PRIVATE);

            String s = userSharedPref.getString("user_id", "DEFAULT");
            Log.d("AHHHHHH", s);
            if (s.equals("DEFAULT") == false)
                userID = s;





        //queue for requests
        queue = Volley.newRequestQueue(this);

        //get storeid
        storeID = getIntent().getIntExtra("storeID", 0);

        //goes to method to add review with store id passed in
        btnSubmit.setOnClickListener(e -> { submitReview(storeID);
            Toast.makeText(this, "Review submitted", Toast.LENGTH_SHORT).show();
            finish();
        });

        //seekbar listener that updates the ratings bar
        //visually when the user changes the seekbar
        sbRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                //set ratings bar based on seek bar
                rbRating.setRating(sbRating.getProgress());
            }
        });
    }

    public void submitReview(int storeID) {
        if (storeID != 0) {
            // Create JSON object
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", userID);
                jsonBody.put("store_id", storeID);
                jsonBody.put("description", etReview.getText()+"");
                jsonBody.put("rating", sbRating.getProgress());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // set loader visible
            pbAdd.setVisibility(ProgressBar.VISIBLE);

            String url = "https://pandaexpress-rating-backend-group5.onrender.com/rating";
            // Create POST request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, response -> {
                try {
                    // Get response from API
                    String strResponse = response.getString("message");


                    // Display message
                    Toast.makeText(this, strResponse, Toast.LENGTH_SHORT).show();

                    // Check if response is success
                    /* if (strResponse.contains("Successfully logged in")) {

                    } */

                } catch (JSONException e) {
                    pbAdd.setVisibility(ProgressBar.INVISIBLE);
                    e.printStackTrace();
                }
            }, error -> {
                // Display error message
                Toast.makeText(this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();

                pbAdd.setVisibility(ProgressBar.INVISIBLE);
                Log.e("Volley", error.toString());
            });

            // Add request to queue
            queue.add(jsonObjectRequest);
        }
    }
}