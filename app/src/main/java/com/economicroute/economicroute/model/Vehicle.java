package com.economicroute.economicroute.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Vehicle extends RealmObject {
    @Index
    @PrimaryKey
    private int id;
    private String year;
    private String type;
    private String brand;
    private String name;
    private String plate;
    private double tank;
    private double consumption;
    private double fuel_quantity;
    private boolean isBeingUsed;
    private RealmList<Fuel> fuel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYear() { return year; }

    public void setYear(String year) { this.year = year; }

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

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public double getTank() {
        return tank;
    }

    public void setTank(double tank) {
        this.tank = tank;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getFuel_quantity() {
        return fuel_quantity;
    }

    public void setFuel_quantity(double fuel_quantity) {
        this.fuel_quantity = fuel_quantity;
    }

    public boolean isBeingUsed() { return isBeingUsed; }

    public void setBeingUsed(boolean beingUsed) { isBeingUsed = beingUsed; }

    public RealmList<Fuel> getFuel() {
        return fuel;
    }

    public void setFuel(RealmList<Fuel> fuel) {
        this.fuel = fuel;
    }
}
