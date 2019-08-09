package com.yingxuan.stationerystore.department;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DelegateFrag extends Fragment
        implements View.OnClickListener, AsyncToServer.IServerResponse,
        AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private TextView errorMsgView;

    private List<String> empIds;
    private List<String> empNames;
    private String selectedEmpId;
    private Date start;
    private Date end;

    private Calendar cldr;
    private int day, month, year;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.delegate_frag, container, false);

        errorMsgView = view.findViewById(R.id.error_del);
        errorMsgView.setVisibility(View.GONE);

        // get today's date for use in DatePicker
        cldr = Calendar.getInstance();
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);

        // date fields are not for typing, input using DatePicker
        final EditText startDateView = view.findViewById(R.id.start_date_picker);
        startDateView.setInputType(InputType.TYPE_NULL);
        startDateView.setOnClickListener(this);
        startDateView.setFocusable(false);

        final EditText endDateView = view.findViewById(R.id.end_date_picker);
        endDateView.setInputType(InputType.TYPE_NULL);
        endDateView.setOnClickListener(this);
        endDateView.setFocusable(false);

        // submit button
        Button delBtn = view.findViewById(R.id.del_confirm_btn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // check that both dates have been input
                    String startString = startDateView.getText().toString();
                    String endString = endDateView.getText().toString();
                    if (startString.equals("") || endString.equals("")) {
                        errorMsgView.setText(R.string.error_del_date_empty);
                        errorMsgView.setVisibility(View.VISIBLE);
                        return;
                    }

                    // check that end date is equal or after start date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    start = sdf.parse(startString);
                    end = sdf.parse(endString);
                    if (end.compareTo(start) < 0) {
                        errorMsgView.setText(R.string.error_del_date_invalid);
                        errorMsgView.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // dialog alert for user to confirm
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_del_title)
                        .setMessage(R.string.dialog_del_msg)
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
        });

        // spinner to display employees
        spinner = view.findViewById(R.id.del_emp_list);
        spinner.setOnItemSelectedListener(this);

        // get department employees from server to display
        JSONObject data = new JSONObject();
        try {
            data.put("departmentId", User.departmentId);
        } catch (Exception e){
            e.printStackTrace();
        }
        Command cmd = new Command(this, "get","/DeptDelegation/Index", data);
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
                empIds = new ArrayList<>();
                empNames = new ArrayList<>();
                JSONArray empArray = jsonObj.getJSONArray("employees");

                for (int i=0; i<empArray.length(); i++) {
                    JSONObject emp = empArray.getJSONObject(i);
                    String id = emp.getString("Id");
                    String name = emp.getString("Name");
                    empIds.add(id);
                    empNames.add(name);
                }
            }
            // after successfully delegating employee, redirect to another fragment
            else if (context.compareTo("set") == 0) {
                String status = jsonObj.getString("status");

                if (status.equals("ok")) {
                    Toast.makeText(getContext(), R.string.toast_del,
                            Toast.LENGTH_LONG).show();

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

        // Create an ArrayAdapter using the list of items and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, empNames);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    // DatePicker pops up when either of the date fields are clicked
    @Override
    public void onClick(final View v) {
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String d = String.format("%02d", dayOfMonth);
                        String m = String.format("%02d", monthOfYear + 1);
                        EditText editView = (EditText) v;
                        editView.setText(d + "/" + m + "/" + year);
                    }
                }, year, month, day);
        picker.getDatePicker().setMinDate(cldr.getTimeInMillis());
        picker.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedEmpId = empIds.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // convert delegation details to JSONObject to send to server
    private void submit() {
        JSONObject data = new JSONObject();
        try {
            data.put("dateFrom", start.toGMTString());
            data.put("dateTo", end.toGMTString());
            data.put("employeeId", selectedEmpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Command cmd = new Command(this, "set","/DeptDelegation/Submit", data);
        new AsyncToServer().execute(cmd);
    }
}
