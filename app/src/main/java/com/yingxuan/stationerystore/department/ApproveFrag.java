package com.yingxuan.stationerystore.department;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.yingxuan.stationerystore.model.Item;
import com.yingxuan.stationerystore.model.RequisitionDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ApproveFrag extends Fragment implements View.OnClickListener, AsyncToServer.IServerResponse {

    private List<RequisitionDetails> reqDetails_list;
    private TextView formid, formsubBy, status, formdate;
    private ArrayList<Item> ordereditems;
    private ScrollView itemTable;
    private EditText commentbox;
    private Button approveBtn;
    private Button rejectBtn;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //inflate layout
        View view = inflater.inflate(R.layout.approve_dtl_frag, container, false);

        formid = view.findViewById(R.id.apv_reqNo);
        status = view.findViewById(R.id.apv_status);
        formsubBy = view.findViewById(R.id.apv_submitted_by);
        formdate = view.findViewById(R.id.apv_dos);

        //handler for UI elements
        commentbox = view.findViewById(R.id.cmtbx);
        commentbox.setOnClickListener(this);
        approveBtn = view.findViewById(R.id.apv_btn);
        approveBtn.setOnClickListener(this);
        rejectBtn = view.findViewById(R.id.rj_btn);
        rejectBtn.setOnClickListener(this);

        //retrieving data using bundle
        Bundle bundle = getArguments();

        formid.setText(String.valueOf(bundle.getString("id")));
        status.setText("Pending Approval");
        formsubBy.setText(String.valueOf(bundle.getString("name")));
        formdate.setText(String.valueOf(bundle.getString("date")));

        // get details from server
        JSONObject data = new JSONObject();
        try {
            data.put("id", String.valueOf(bundle.getString("id")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Command cmd = new Command(this, "get", "/deptapproverequisitions/ViewApproveRequisition", data);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {

        if (jsonObj == null) {
            return;
        }

        try {
            String context = (String) jsonObj.get("context");

            // Convert JSON to an ArrayList of item
            if (context.compareTo("get") == 0) {
                JSONArray selectItems = jsonObj.getJSONArray("filtered_items");
                JSONArray reqDetailsArray = jsonObj.getJSONArray("requisitiondetails");
                ordereditems = new ArrayList<Item>();

                for (int i = 0; i <= (reqDetailsArray.length() - 1); i++) {

                    JSONObject rqItem = reqDetailsArray.getJSONObject(i);
                    JSONObject item = selectItems.getJSONObject(i);

                    Item rqDetail = new Item();
                    rqDetail.setName(item.getString("Description"));
                    rqDetail.setUom(item.getString("Unit"));
                    rqDetail.setQuantity(rqItem.getInt("Quantity"));

                    ordereditems.add(rqDetail);
                }
            }
            else {
                String msg = "";
                if (context.compareTo("setApprove") == 0)
                    msg = "Form approved.";
                else if (context.compareTo("setReject") == 0)
                    msg = "Form rejected.";

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                redirect();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Context appContext = getActivity().getApplicationContext();

        // add items into TableLayout row by row
        TableLayout tableLayout = getView().findViewById(R.id.ordItemsRow);
        tableLayout.setVisibility(View.VISIBLE);

        for (Item oi : ordereditems) {
            // Create a new table row.
            TableRow tableRow = new TableRow(appContext);

            // Set new table row layout parameters
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 24, 0, 24);
            tableRow.setLayoutParams(layoutParams);

            // Add a TextView in each column
            TextView textView = new TextView(appContext);
            textView.setText(oi.getName());
            tableRow.addView(textView, 0);

            textView = new TextView(appContext);
            textView.setText(oi.getUom());
            tableRow.addView(textView, 1);

            textView = new TextView(appContext);
            textView.setText(Integer.toString(oi.getQuantity()));
            tableRow.addView(textView, 2);

            tableLayout.addView(tableRow);
        }
    }

    @Override
    public void onClick(View v) {

        try {
            final EditText commentbox = getView().findViewById(R.id.cmtbx);
            final String comment;
            Bundle bundle = getArguments();
            itemTable = getView().findViewById(R.id.orditems_scroll);

            comment = commentbox.getText().toString();

            final String id = String.valueOf(bundle.getString("id"));

            switch (v.getId()) {

                case R.id.apv_btn:

                    JSONObject data = submit(comment, id);
                    Command cmd = new Command(this, "setApprove", "/DeptApproveRequisitions/AcceptRequisitionForm", data);
                    new AsyncToServer().execute(cmd);
                    break;

                case R.id.rj_btn:
                    JSONObject data2 = submit(comment, id);
                    Command cmd2 = new Command(this, "setReject", "/DeptApproveRequisitions/RejectRequisitionForm", data2);
                    new AsyncToServer().execute(cmd2);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private JSONObject submit(String comment, String id) {

        JSONObject data = new JSONObject();

        try {
            data.put("comment", comment);
            data.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return data;
    }

    private void redirect() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        Fragment frag = new ApproveIndexFrag();
        trans.replace(R.id.frag, frag);
        trans.commit();
    }
}



