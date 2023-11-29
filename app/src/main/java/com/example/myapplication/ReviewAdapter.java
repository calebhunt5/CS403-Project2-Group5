/* Danielle Smith
    11/29/23
    Adapter class for reviews on extended view to be used with recycler
 */

package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    ArrayList<PandaReview> reviews;
    Context context;

    /*
        TODO
        * LoadMore functionality like we had in D&D app
     */

    public ReviewAdapter(Context context, ArrayList<PandaReview> arrSpells) {
        this.reviews = arrSpells;
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

        /*
            TODO
            * Set stars based on review
            * On click for like & more like functionality
         */

        //set elements in layout
        holder.tvName.setText(review.userName);
        holder.tvLikes.setText(review.likes+"");

        //set stars image based on likes
        switch (review.rating) {
            case 1:
                holder.ivStars.setImageResource(R.drawable.one_star);
                break;
            case 2:
                holder.ivStars.setImageResource(R.drawable.two_stars);
                break;
            case 3:
                holder.ivStars.setImageResource(R.drawable.three_stars);
                break;
            case 4:
                holder.ivStars.setImageResource(R.drawable.four_stars);
                break;
            case 5:
                holder.ivStars.setImageResource(R.drawable.five_stars);
                break;
            default:
                holder.ivStars.setImageResource(R.drawable.zero_stars);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout reviewLayout;
        TextView tvName, tvLikes;
        ToggleButton tbLike;
        ImageView ivStars;

        public ReviewViewHolder(@NonNull View view) {
            super(view);

            //get elements from layout
            reviewLayout = view.findViewById(R.id.reviewLayout);
            tvName = view.findViewById(R.id.tvName);
            tvLikes = view.findViewById(R.id.tvLikes);
            tbLike = view.findViewById(R.id.tbLike);
            ivStars = view.findViewById(R.id.ivStars);
        }
    }
}
