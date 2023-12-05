/* Danielle Smith
    12/4/23
    PandaLocation class to be used on map view
    Allows for easy display and easy passing of
    the storeID to go to extended view
 */

package com.example.myapplication;

public class PandaLocation {
    int storeID;
    double averageRating;
    double lat;
    double lng;
    String address;

    public PandaLocation(int storeID, double averageRating, double lat, double lng) {
        this.storeID = storeID;
        this.averageRating = averageRating;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }
}
