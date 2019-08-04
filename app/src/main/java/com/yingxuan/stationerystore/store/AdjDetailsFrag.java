package com.yingxuan.stationerystore.store;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.model.Adjustment;
import com.yingxuan.stationerystore.model.AdjustmentDetails;
import com.yingxuan.stationerystore.model.Retrieval;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdjDetailsFrag extends Fragment implements AsyncToServer.IServerResponse{

    private List<AdjustmentDetails> adjList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.adj_details_frag, container, false);

        // voucher info gotten from item click in previous page
        Adjustment adj = null;
        Bundle bundle = getArguments();
        if (bundle != null)
            adj = (Adjustment) bundle.getSerializable("adj");

        // display voucher info
        TextView textView = view.findViewById(R.id.voucher_no);
        textView.setText(adj.getId());
        textView = view.findViewById(R.id.date_submitted);
        textView.setText(adj.getDateString());
        textView = view.findViewById(R.id.submitted_by);
        textView.setText(adj.getSubmittedBy());
        textView = view.findViewById(R.id.total_value);
        String totalValue = "$" + Math.abs(adj.getTotalValue());
        textView.setText(totalValue);
        textView = view.findViewById(R.id.status);
        textView.setText(adj.getStatus());

        // get adjustment details from server
        JSONObject data = new JSONObject();
        try {
            data.put("sessionId", User.sessionId);
            data.put("voucherId", adj.get("id"));
        } catch (Exception e){
            e.printStackTrace();
        }
        Command cmd = new Command(this, "get","/StoreInventoryAdjustment/Details", data);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        // Convert JSON to an ArrayList of adjustment details
        try {
            adjList = new ArrayList<AdjustmentDetails>();
            JSONArray adjDetails = jsonObj.getJSONArray("adjDetails");

            for (int i=0; i<adjDetails.length(); i++) {
                JSONObject item = adjDetails.getJSONObject(i);

                AdjustmentDetails adjDetail = new AdjustmentDetails();
                adjDetail.setItemId(item.getString("ItemId"));
                adjDetail.setDescription(item.getString("ItemDescription"));
                adjDetail.setQtyAdjusted(item.getInt("QtyAdjusted"));
                adjDetail.setReason(item.getString("Reason"));

                adjList.add(adjDetail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Context appContext = getActivity().getApplicationContext();

        // add items into TableLayout row by row
        TableLayout tableLayout = getView().findViewById(R.id.adj_table);

        for (AdjustmentDetails adj : adjList) {
            // Create a new table row.
            TableRow tableRow = new TableRow(appContext);

            // Set new table row layout parameters
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(layoutParams);

            // Add a TextView in each column
            TextView textView = new TextView(appContext);
            textView.setText(adj.getItemId());
            tableRow.addView(textView, 0);

            textView = new TextView(appContext);
            textView.setText(adj.getDescription());
            tableRow.addView(textView, 1);

            textView = new TextView(appContext);
            textView.setText(Integer.toString(adj.getQtyAdjusted()));
            tableRow.addView(textView, 2);

            textView = new TextView(appContext);
            textView.setText(adj.getReason());
            tableRow.addView(textView, 3);

            tableLayout.addView(tableRow);
        }
    }
}
