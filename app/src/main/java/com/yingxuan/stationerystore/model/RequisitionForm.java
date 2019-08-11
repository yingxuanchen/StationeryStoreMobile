package com.yingxuan.stationerystore.model;

import java.util.HashMap;

public class RequisitionForm extends HashMap<String, Object> {

    private String rId;
    private String date;
    private String empName;


    public RequisitionForm(String Id, String date, String empName) {

        this.rId = Id;
        this.date = date;
        this.empName = empName;

        this.put("rid", rId);
        this.put("date", date);
        this.put("empName", empName);

    }

    //getters
    public String getrId() {
        return rId;
    }

    public String getEmpName() {
        return empName;
    }

    public String getDate() {
        return date;
    }


    //setters
    public void setrId(String rId) {
        this.rId = rId;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
