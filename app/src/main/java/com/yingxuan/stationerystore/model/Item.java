package com.yingxuan.stationerystore.model;

public class Item {

    private String name;
    private String uom;
    private int quantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item() {
    }

    public Item(String name, String uom, int quantity) {
        this.name = name;
        this.uom = uom;
        this.quantity = quantity;
    }


}


