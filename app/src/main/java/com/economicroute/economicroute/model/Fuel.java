package com.economicroute.economicroute.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Fuel extends RealmObject {
    @PrimaryKey
    private int id;
    private int name;
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
