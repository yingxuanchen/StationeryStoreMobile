package com.yingxuan.stationerystore;

public class Retrieval {
    private String itemId;
    private String description;
    private int binNumber;
    private String unit;
    private int quantityNueeded;
    private int quantityRetrieved;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(int binNumber) {
        this.binNumber = binNumber;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantityNueeded() {
        return quantityNueeded;
    }

    public void setQuantityNueeded(int quantityNueeded) {
        this.quantityNueeded = quantityNueeded;
    }

    public int getQuantityRetrieved() {
        return quantityRetrieved;
    }

    public void setQuantityRetrieved(int quantityRetrieved) {
        this.quantityRetrieved = quantityRetrieved;
    }
}
