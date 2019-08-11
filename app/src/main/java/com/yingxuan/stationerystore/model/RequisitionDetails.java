package com.yingxuan.stationerystore.model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RequisitionDetails {

    private String id;
    private String itemid;
    private Integer quantity;
    private String employeeId;
    private String departmentId;
    private LocalDate dateCreated;
    private String status;
    private LocalDate dateApproved;
    private String itemName;
    private String UOM;
    private int Quantity;
    private String remarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDateCreated() {

        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new Date());
    }

    public String setDateCreated(String dateCreated) {
        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateApproved() {

        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new Date());
    }

    public String setDateApproved(String dateApproved) {
        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").toString();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Constructor
     **/

    public RequisitionDetails() {
    }

    public RequisitionDetails(String id, String employeeId, String dateCreated) {
        this.id = id;
        this.employeeId = employeeId;
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss");
        this.dateCreated = LocalDate.parse(dateCreated, dtf);
    }

    public RequisitionDetails(String itemName, String UOM, int quantity) {
        this.itemName = itemName;
        this.UOM = UOM;
        Quantity = quantity;
    }
}
