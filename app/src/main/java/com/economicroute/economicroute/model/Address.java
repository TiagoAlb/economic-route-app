package com.economicroute.economicroute.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;
import io.realm.annotations.PrimaryKey;

public class Address extends RealmObject {
    @PrimaryKey
    private long id;
    @Required
    private String street;
    @Required
    private String zipCode;
    @Required
    private String number;
    private String neighborhood;
    @Required
    private String city;
    private double lat;
    private double lng;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getStreet() { return street; }

    public void setStreet(String street) { this.street = street; }

    public String getZipCode() { return zipCode; }

    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getNumber() { return number; }

    public void setNumber(String number) { this.number = number; }

    public String getNeighborhood() { return neighborhood; }

    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public double getLat() { return lat; }

    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }

    public void setLng(double lng) { this.lng = lng; }

}
