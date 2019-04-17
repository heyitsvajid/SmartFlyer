package com.smartflyer;

/**
 * Data model for each row of the RecyclerView
 */
public class Airport {

    // Member variables.

    private String name;
    private String _id;
    private String city;
    private String country;
    private String iata;
    private String latitude;
    private String longitude;
    private String[] waitTimes;
    private String image;
    private String averageWaitTime;

    public Airport(String _id,String name, String city, String country, String iata, String latitude, String longitude, String image, String averageWaitTime) {
        this._id = _id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.iata = iata;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.averageWaitTime = averageWaitTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String icao) {
        this.iata = icao;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String[] getWaitTimes() {
        return waitTimes;
    }

    public void setWaitTimes(String[] waitTimes) {
        this.waitTimes = waitTimes;
    }

    public String getAverageWaitTime() {
        return averageWaitTime;
    }

    public void setAverageWaitTime(String averageWaitTime) { this.averageWaitTime = averageWaitTime; }

}