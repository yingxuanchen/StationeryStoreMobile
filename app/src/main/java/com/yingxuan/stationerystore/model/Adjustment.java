package com.yingxuan.stationerystore.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Adjustment extends HashMap<String, Object> {
    private String id;
    private Date dateSubmitted;
    private String dateString;
    private String submittedBy;
    private String status;
    private int totalValue;

    public Adjustment(String id, String date, String submittedBy, String status, int totalValue) {
        Date dateSubmitted = null;
        String dateString = "";
        try {
            dateSubmitted = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            dateString = new SimpleDateFormat("dd/MM/yyyy").format(dateSubmitted);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.id = id;
        this.dateSubmitted = dateSubmitted;
        this.dateString = dateString;
        this.submittedBy = submittedBy;
        this.status = status;
        this.totalValue = totalValue;

        this.put("id", id);
        this.put("dateSubmitted", dateSubmitted);
        this.put("dateString", dateString);
        this.put("submittedBy", submittedBy);
        this.put("status", status);
        this.put("totalValue", totalValue);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }
}
