package com.yingxuan.stationerystore.department;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yingxuan.stationerystore.MainActivity;
import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RepFrag extends Fragment
        implements AsyncToServer.IServerResponse, View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private TextView currentRepView;
    private List<String> empNames;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rep_frag, container, false);

        currentRepView = view.findViewById(R.id.rep_current);

        // save button
        Button repBtn = view.findViewById(R.id.rep_save_btn);
        repBtn.setOnClickListener(this);

        // spinner to display employees
        spinner = view.findViewById(R.id.rep_emp_list);
        spinner.setOnItemSelectedListener(this);

        // get current rep and department employees from server to display
        Command cmd = new Command(this, "get","/DeptAssignRep/Index", null);
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
            String context = jsonObj.getString("context");

            // get current rep and department employees from server to display
            if (context.compareTo("get") == 0) {
                String currentRep = jsonObj.getString("deptRepName");
                currentRepView.setText(currentRep);

                empNames = new ArrayList<String>();
                JSONArray empArray = jsonObj.getJSONArray("employees");

                for (int i=0; i<empArray.length(); i++) {
                    JSONObject emp = empArray.getJSONObject(i);
                    String name = emp.getString("Name");
                    empNames.add(name);
                }
            }
            // after successfully changing rep, reload the fragment
            else if (context.compareTo("set") == 0) {
                String status = jsonObj.getString("status");

                if (status.equals("ok")) {
                    Toast.makeText(getContext(), R.string.toast_rep,
                            Toast.LENGTH_LONG).show();

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = fm.beginTransaction();
                    trans.detach(this).attach(this).commit();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create an ArrayAdapter using the list of items and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, empNames);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(final View v) {
        // dialog alert for user to confirm
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_rep_title)
                .setMessage(R.string.dialog_rep_msg)
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

    // get new rep and send to server to update
    private void submit() {
        JSONObject data = new JSONObject();
        try {
            data.put("employeeName", spinner.getSelectedItem().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Command cmd = new Command(this, "set","/DeptAssignRep/Update", data);
        new AsyncToServer().execute(cmd);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
