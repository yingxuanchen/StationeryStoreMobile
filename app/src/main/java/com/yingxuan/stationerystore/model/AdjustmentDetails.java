package com.yingxuan.stationerystore.model;

public class AdjustmentDetails {
    private String itemId;
    private String description;
    private int qtyAdjusted;
    private String reason;

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

    public int getQtyAdjusted() {
        return qtyAdjusted;
    }

    public void setQtyAdjusted(int qtyAdjusted) {
        this.qtyAdjusted = qtyAdjusted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
