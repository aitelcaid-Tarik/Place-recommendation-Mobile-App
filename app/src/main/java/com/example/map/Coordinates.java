package com.example.map;

public class Coordinates {


    String name;
    String description;
    String latitude;
    String longitude;
    String rating;


    public Coordinates(String name, String description, String lat, String lon, String rating) {
        this.name = name;
        this.description = description;
        this.latitude = lat;
        this.longitude = lon;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "[ name : " + name + ", description : " + description + ", latitude : " + latitude + ", longitude : " + longitude + ", rating : " + rating + " ]";
    }
}
