/* Danielle Smith
    11/29/23
    Adapter class for reviews on extended view to be used with recycler
 */

package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    ArrayList<PandaReview> reviews;
    Context context;

    public ReviewAdapter(Context context, ArrayList<PandaReview> pandaReviews) {
        this.reviews = pandaReviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_panda_review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // get review
        PandaReview review = reviews.get(position);

        SharedPreferences userSharedPref = context.getSharedPreferences("saved_session", Context.MODE_PRIVATE);
        String s = userSharedPref.getString("user_id", "DEFAULT");

        Log.d("viewholder", review.toString());

        /*
            * On click for like & more like functionality
         */

        //set elements in layout
        holder.tvName.setText(review.userName);
        holder.tvDesc.setText(review.description);
        holder.tvLikes.setText(review.likes+"");
        holder.tbLike.setChecked(review.liked);
        holder.userRatingBar.setRating((float) review.rating);


        RequestQueue queue = Volley.newRequestQueue(context);

        // Add like functionality
        holder.tbLike.setOnClickListener(view -> {
            // user id and rating id
            holder.tbLike.setChecked(!holder.tbLike.isChecked());

            // Create JSON object
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("user_id", s);
                jsonBody.put("ratingID", review._id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "https://pandaexpress-rating-backend-group5.onrender.com/likedRating";
            // Create POST request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, response -> {
                try {
                    // Get response from API
                    String strResponse = response.getString("message");

                    // Check if response is success
                    /* if (strResponse.contains("Successfully logged in")) {

                    } */

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.e("Volley", error.toString());
            });

            // Add request to queue
            queue.add(jsonObjectRequest);

            int intLikeNumber = Integer.parseInt(holder.tvLikes.getText().toString()) + 1;

            // update likes
            holder.tvLikes.setText(intLikeNumber +"");
        });

        // TM: Extras


    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout reviewLayout;
        TextView tvName, tvLikes, tvDesc;

        ToggleButton tbLike;
        RatingBar userRatingBar;

        public ReviewViewHolder(@NonNull View view) {
            super(view);

            //get elements from layout
            reviewLayout = view.findViewById(R.id.reviewLayout);
            tvName = view.findViewById(R.id.tvName);
            tvDesc = view.findViewById(R.id.tvDesc);
            tvLikes = view.findViewById(R.id.tvLikes);
            tbLike = view.findViewById(R.id.tbLike);
            userRatingBar = view.findViewById(R.id.userRatingBar);
        }
    }
}
