package com.yingxuan.stationerystore.department;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.model.Employee;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DelegateFrag extends Fragment
        implements View.OnClickListener, AsyncToServer.IServerResponse{

    private List<Employee> empList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.delegate_frag, container, false);

        Button delBtn = view.findViewById(R.id.del_confirm_btn);
        delBtn.setOnClickListener(this);

        // get department employees from server to display
        JSONObject data = new JSONObject();
        try {
            data.put("departmentId", User.departmentId);
        } catch (Exception e){
            e.printStackTrace();
        }
        Command cmd = new Command(this, "get","/DeptDelegation/Index", null);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        try {
            String context = jsonObj.getString("context");

            // get department employees from server to display
            if (context.compareTo("get") == 0) {
                // convert JSON to list of employees
                empList = new ArrayList<Employee>();
                JSONArray empArray = jsonObj.getJSONArray("employees");

                for (int i=0; i<empArray.length(); i++) {
                    JSONObject emp = empArray.getJSONObject(i);
                    String id = emp.getString("Id");
                    String name = emp.getString("Name");
                    empList.add(new Employee(id, name));
                }
            }
            // after successfully delegating employee, redirect to another fragment
            else if (context.compareTo("set") == 0) {
                String status = jsonObj.getString("status");

                if (status.equals("ok")) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = fm.beginTransaction();
                    Fragment frag = new DelegateCancelFrag();
                    trans.replace(R.id.frag, frag);
                    trans.commit();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
