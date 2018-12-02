package com.economicroute.economicroute.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

public class Gas_station {
    private int id;
    private String name;
    private String address;
    private String phoneNum;
    private double price_gas;
    private LatLng location;
    private int price_priority;

    public Gas_station (String name, double price_gas, LatLng location){
        this.name = name;
        this.price_gas = price_gas;
        this.location = location;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
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

    public int findPriorityGasStation(List<Gas_station> gas_stations) {

        double best_price=gas_stations.get(0).getPrice_gas();
        double worst_price=0;
        double price_factor;
        for (int i=0; i<gas_stations.size(); i++){
            if (gas_stations.get(i).getPrice_gas()>=worst_price){
                worst_price = gas_stations.get(i).getPrice_gas();
            }
            if (gas_stations.get(i).getPrice_gas()<=best_price){
                best_price = gas_stations.get(i).getPrice_gas();
            }
        }
        price_factor=(worst_price+best_price)/2;
        double middle_final=(worst_price+price_factor)/2;
        double middle_initial=(best_price+price_factor)/2;

        if (getPrice_gas()<=middle_initial)
            return 1;
        else if ((getPrice_gas()>middle_initial)&&(getPrice_gas()<=middle_final))
            return 2;
        else
            return 3;
    }
}
