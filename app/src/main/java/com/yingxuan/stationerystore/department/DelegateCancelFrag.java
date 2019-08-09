package com.yingxuan.stationerystore.department;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class DelegateCancelFrag extends Fragment
        implements AsyncToServer.IServerResponse, View.OnClickListener {

    private TextView startView;
    private TextView endView;
    private TextView empView;

    private String empId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.delegate_cancel_frag, container, false);

        startView = view.findViewById(R.id.start_date);
        endView = view.findViewById(R.id.end_date);
        empView = view.findViewById(R.id.emp);

        Button cancelBtn = view.findViewById(R.id.del_cancel_btn);
        cancelBtn.setOnClickListener(this);

        // get delegation from server to display
        JSONObject data = new JSONObject();
        try {
            data.put("departmentId", User.departmentId);
        } catch (Exception e){
            e.printStackTrace();
        }
        Command cmd = new Command(this, "get","/DeptDelegation/Assigned", data);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        try {
            String context = jsonObj.getString("context");

            // get delegation from server to display
            if (context.compareTo("get") == 0) {
                empId = jsonObj.getString("employeeId");
                String name = jsonObj.getString("employeeName");
                String dateFrom = jsonObj.getString("dateFrom");
                String dateTo = jsonObj.getString("dateTo");

                empView.setText(name);
                startView.setText(dateFrom);
                endView.setText(dateTo);
            }
            // after successfully cancelling delegation, redirect to another fragment
            else if (context.compareTo("set") == 0) {
                String status = jsonObj.getString("status");

                if (status.equals("ok")) {
                    Toast.makeText(getContext(), R.string.toast_del_cancel,
                            Toast.LENGTH_LONG).show();

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = fm.beginTransaction();
                    Fragment frag = new DelegateFrag();
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
        // dialog alert for user to confirm
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_del_cancel_title)
                .setMessage(R.string.dialog_del_cancel_msg)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submit();
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // convert delegation details to JSONObject to send to server
    private void submit() {
        JSONObject data = new JSONObject();
        try {
            data.put("employeeId", empId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Command cmd = new Command(this, "set","/DeptDelegation/Cancel", data);
        new AsyncToServer().execute(cmd);
    }
}
