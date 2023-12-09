package com.example.myapplication;

public class Panda {

    String id;
    String Address;
    String FullAddress;
    double lat;
    double lon;
    double distance;
    double rating;

    public Panda() {}



    public Panda(String address, double lat, double lon, double distance, double rating) {
        Address = address;
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
        this.rating = rating;
    }

    public Panda(String id, double lat, double lon, double rating) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.rating = rating;
    }

    public Panda(String id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public Panda(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    //setters
    public void setAddress(String address) {
        Address = address;
    }

    public void setFullAddress(String fullAddress) {FullAddress = fullAddress;}

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    //getters
    public String getAddress() {return Address;}

    public String getFullAddress() {return FullAddress;}

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getDistance() {
        return distance;
    }

    public double getRating() {
        return rating;
    }
}
