package com.yingxuan.stationerystore.model;

import java.util.HashMap;

public class RequisitionForm extends HashMap<String, Object> {

    private String rId;
    private String empName;
    private String date;

    public RequisitionForm(String rId, String date, String empName) {

        this.put("id", rId);
        this.put("empName", empName);
        this.put("dateSubmitted", date);

    }

    public String getrId() {
        return rId;
    }

    public String getEmpName() {
        return empName;
    }

    public String getDate() {
        return date;
    }


}
