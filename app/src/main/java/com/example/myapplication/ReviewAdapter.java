/* Danielle Smith
    11/29/23
    Adapter class for reviews on extended view to be used with recycler
 */

package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    ArrayList<PandaReview> reviews;
    Context context;
    CookieManager cookieManager;

    public ReviewAdapter(Context context, ArrayList<PandaReview> pandaReviews) {
        this.reviews = pandaReviews;
        this.context = context;

        //cookie manager to help get id of currently logged in user
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
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


        List<HttpCookie> test = cookieManager.getCookieStore().getCookies();

        Log.d("cookieholder", test.toString());

        /*
            * On click for like & more like functionality
         */

        //set elements in layout
        holder.tvName.setText(review.userName);
        holder.tvDesc.setText(review.description);
        holder.tvLikes.setText(review.likes+"");
        holder.userRatingBar.setRating((float) review.rating);

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
