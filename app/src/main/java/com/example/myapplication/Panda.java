package com.example.myapplication;

public class Panda {

    String Address;
    int lat;
    int lon;
    double distance;
    int rating;

    public Panda(String address, int lat, int lon, double distance, int rating) {
        Address = address;
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
        this.rating = rating;
    }

    public Panda(int lat, int lon) {
        this.lat = lat;
        this.lon = lon;
    }

    //setters
    public void setAddress(String address) {
        Address = address;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    //getters
    public String getAddress() {
        return Address;
    }

    public int getLat() {
        return lat;
    }

    public int getLon() {
        return lon;
    }

    public double getDistance() {
        return distance;
    }

    public int getRating() {
        return rating;
    }
}
