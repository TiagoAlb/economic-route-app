package com.economicroute.economicroute.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Gas_station {
    private String name;
    private String address;
    private String hours;
    private String phoneNum;
    private String distance;
    private double price_gas;
    private LatLng location;
    private int price_priority;

    public Gas_station (String name, double price_gas, LatLng location){
        this.name = name;
        this.price_gas = price_gas;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getPrice_gas() {
        return price_gas;
    }

    public void setPrice_gas(double price_gas) {
        this.price_gas = price_gas;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getPrice_priority() {
        return price_priority;
    }

    public void setPrice_priority(int price_priority) {
        this.price_priority = price_priority;
    }
}
