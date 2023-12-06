package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PandaAdapter extends RecyclerView.Adapter<PandaAdapter.PandaViewHolder> {

    ArrayList<Panda> pandas;
    Context context;

    public PandaAdapter(Context context, ArrayList<Panda> pandas) {
        this.pandas = pandas;
        this.context = context;
    }

    @NonNull
    @Override
    public PandaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.location_item, parent, false);
        return new PandaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PandaViewHolder holder, int position) {
        Panda p = pandas.get(position);
        holder.txtAddress.setText(p.getAddress() + "");
        holder.txtDistance.setText(p.getDistance() + " miles away");
    }

    @Override
    public int getItemCount() {
        return pandas.size();
    }

    class PandaViewHolder extends RecyclerView.ViewHolder {

        TextView txtAddress;
        TextView txtDistance;
        RatingBar rbAverageRating;

        public PandaViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            rbAverageRating = itemView.findViewById(R.id.ratingBar2);
        }
    }
}
