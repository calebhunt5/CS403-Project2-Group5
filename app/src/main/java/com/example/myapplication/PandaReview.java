/* Danielle Smith
    11/29/23
    PandaReview class to be used on extended view for recycler
 */

package com.example.myapplication;

public class PandaReview {
    String userName;
    int rating;
    int likes;
    boolean liked;

    public PandaReview(String userName, int rating, int likes, boolean liked) {
        this.userName = userName;
        this.rating = rating;
        this.likes = likes;
        this.liked = liked;
    }

    public boolean isLiked() {
        return liked;
    }
}
