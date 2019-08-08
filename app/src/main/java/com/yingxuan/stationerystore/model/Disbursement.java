package com.yingxuan.stationerystore.model;

import net.sourceforge.jtds.jdbc.DateTime;

import java.util.HashMap;

public class Disbursement extends HashMap<String,String> {
    public Disbursement(String Id,String DateCreated,String DepartmentId,String Status){
        put("Id",Id);
        put("DateCreated",DateCreated);
        put("DepartmentId",DepartmentId);
        put("Status",Status);
    }

}
