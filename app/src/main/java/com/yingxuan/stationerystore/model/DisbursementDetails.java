package com.yingxuan.stationerystore.model;

public class DisbursementDetails {

    private String itemId;
    private String itemName;
    private int qtyRetrieved;
    private int qtyCollected;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQtyRetrieved() {
        return qtyRetrieved;
    }

    public void setQtyRetrieved(int qtyRetrieved) {
        this.qtyRetrieved = qtyRetrieved;
    }

    public int getQtyCollected() {
        return qtyCollected;
    }

    public void setQtyCollected(int qtyCollected) {
        this.qtyCollected = qtyCollected;
    }
}
