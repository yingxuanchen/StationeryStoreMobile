package com.yingxuan.stationerystore.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.yingxuan.stationerystore.MainActivity;
import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.model.Disbursement;
import com.yingxuan.stationerystore.model.MySimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Integer.parseInt;

public class DisbursementFrag extends Fragment
        implements AsyncToServer.IServerResponse,View.OnClickListener {
    private Spinner dropdownlist_dept;
    private Button btn_search;
    private View view;
    private ListView listView;

    private List<Disbursement> disbursements;
    private Disbursement disbursement;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view=inflater.inflate(R.layout.disbursement_frag,container,false);

        dropdownlist_dept=view.findViewById(R.id.spinner_department);
        //dropdownlist_dept.setOnItemSelectedListener(this);
        btn_search=view.findViewById(R.id.search_department);
        btn_search.setOnClickListener(this);
        listView=view.findViewById(R.id.list_department);

        Command cmd = new Command(this,"getdepartmentlist","/StoreDisbursement/GetAllDepartment",null);
        new AsyncToServer().execute(cmd);
        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            // prevent user from being able to press back to access previous session
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        try {
            String context = (String) jsonObj.get("context");

            if (context.compareTo("getdepartmentlist") == 0) {

                List<String> departmentlist = new ArrayList<String>();
                JSONArray itemArray = jsonObj.getJSONArray("deptlist");

                for (int i=0; i<itemArray.length(); i++) {
                    JSONObject item = itemArray.getJSONObject(i);
                    departmentlist.add(item.getString("Name"));
                }

                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,departmentlist);
                dropdownlist_dept.setAdapter(adapter);
            }
            if (context.compareTo("getdisbursement") == 0) {
                String datastatus="";
                ListView listView = view.findViewById(R.id.list_department);

                disbursements = new ArrayList<Disbursement>();

                datastatus = (String) jsonObj.get("status");


                //clear list
                listView.setAdapter(new MySimpleAdapter(getActivity(),disbursements,R.layout.disbursement_row,
                        new String[]{"Id","DateCreated","DepartmentId","Status"},
                        new int[]{R.id.disbursement_id,R.id.date,R.id.department,R.id.status}));

                if(datastatus.equals("empty")){
                    Toast.makeText(getActivity(),"no record",Toast.LENGTH_SHORT).show();
                }
                else{
                    JSONArray itemArray = jsonObj.getJSONArray("list");
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject item = itemArray.getJSONObject(i);
                        disbursement = new Disbursement(item.getString("Id"), StampToDate(item.getString("DateCreated")),
                                item.getString("DepartmentId"), item.getString("Status"));
                        disbursements.add(disbursement);
                    }

                    listView.setAdapter(new MySimpleAdapter(getActivity(), disbursements, R.layout.disbursement_row,
                            new String[]{"Id", "DateCreated", "DepartmentId", "Status"},
                            new int[]{R.id.disbursement_id, R.id.date, R.id.department, R.id.status}));
                    //Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String StampToDate(String timeMillis){
        String s = timeMillis.replace("/Date(","").replace(")/","");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+08"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(s));
        SimpleDateFormat sf = new SimpleDateFormat("dd MMM yyyy");
        String date = sf.format(calendar.getTime());
        return date;
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.search_department){
            String departmentName=dropdownlist_dept.getSelectedItem().toString();

            JSONObject jsonObj = new JSONObject();

            try {
                jsonObj.put("name", departmentName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Command cmd = new Command(this,"getdisbursement","/StoreDisbursement/GetDisbursementByDepartmentName",jsonObj);
            new AsyncToServer().execute(cmd);
        }

    }

}
