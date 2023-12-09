/* Danielle Smith
    11/29/23
    PandaReview class to be used on extended view for recycler
 */

package com.example.myapplication;


public class PandaReview {
    String userName, description, busy;
        // Busy should be of {"Extremely", "Somewhat", "Average", "Not really", "Dead"}
        //  Probably should've used an enum but I figured we could pretty easily just control it from our end
    int rating;
    int likes;
    boolean liked;
    String _id;


    public PandaReview(String userName, String description, int rating, int likes, boolean liked, String _id) {
        this.userName = userName;
        this.description =description;
        this.rating = rating;
        this.likes = likes;
        this.liked = liked;
        this._id = _id;
    }

    public boolean isLiked() {
        return liked;
    }
}
