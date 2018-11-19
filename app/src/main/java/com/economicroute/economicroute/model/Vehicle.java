package com.economicroute.economicroute.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Vehicle extends RealmObject {
    @PrimaryKey
    private int id;
    private String type;
    private String brand;
    private String name;
    private double tank;
    private RealmList<Fuel> fuel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTank() {
        return tank;
    }

    public void setTank(double tank) {
        this.tank = tank;
    }

    public RealmList<Fuel> getFuel() {
        return fuel;
    }

    public void setFuel(RealmList<Fuel> fuel) {
        this.fuel = fuel;
    }
}
