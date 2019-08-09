package com.yingxuan.stationerystore.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.yingxuan.stationerystore.model.AdjustmentDetails;
import com.yingxuan.stationerystore.model.Retrieval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RetrievalFrag extends Fragment
        implements View.OnClickListener, AsyncToServer.IServerResponse {

    private List<Retrieval> retrievals;
    private TextView errorView;
    private boolean flag = false;
    private ArrayList<AdjustmentDetails> adjDetails;
    private JSONArray retrievalArray;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.retrieval_frag, container, false);

        errorView = view.findViewById(R.id.error_retrieve);
        errorView.setVisibility(View.GONE);

        Button retrieveBtn = view.findViewById(R.id.retrieve_btn);
        retrieveBtn.setOnClickListener(this);

        // get retrieval form from server
        Command cmd = new Command(this, "get","/StoreRetrieval/GetRetrieval", null);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        try {
            String context = (String) jsonObj.get("context");

            if (context.compareTo("get") == 0)
            {
                // Convert JSON to an ArrayList of retrieval items
                retrievals = new ArrayList<Retrieval>();
                JSONArray itemArray = jsonObj.getJSONArray("retrievals");

                for (int i=0; i<itemArray.length(); i++) {
                    JSONObject item = itemArray.getJSONObject(i);

                    Retrieval retrieval = new Retrieval();
                    retrieval.setItemId(item.getString("ItemId"));
                    retrieval.setDescription(item.getString("Description"));
                    retrieval.setBinNumber(item.getInt("BinNumber"));
                    retrieval.setUnit(item.getString("Unit"));
                    retrieval.setQuantityNeeded(item.getInt("QuantityNeeded"));

                    retrievals.add(retrieval);
                }
            }
            else if (context.compareTo("set") == 0)
            {
                String status = jsonObj.getString("status");

                if (status.equals("ok")) {
                    Toast.makeText(getContext(), R.string.toast_retrieve,
                            Toast.LENGTH_LONG).show();

                    // go to create new adjustment voucher page if any quantity retrieved is less than needed
                    if (flag) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("adjDetails", adjDetails);
                        Fragment frag = new AdjAutoFrag();
                        frag.setArguments(bundle);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction trans = fm.beginTransaction();
                        trans.replace(R.id.frag, frag);
                        trans.addToBackStack(null);
                        trans.commit();
                    }
                    // go to disbursement page if retrieve is successful and no adjustment voucher needed
                    else {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction trans = fm.beginTransaction();
                        Fragment frag = new DisbursementFrag();
                        trans.replace(R.id.frag, frag);
                        trans.commit();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Context appContext = getActivity().getApplicationContext();

        // add items into TableLayout row by row
        TableLayout tableLayout = getView().findViewById(R.id.retrieve_table);

        for (Retrieval retrieval : retrievals) {
            // Create a new table row.
            TableRow tableRow = new TableRow(appContext);

            // Set new table row layout parameters
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(layoutParams);

            // Add a TextView in each column
            TextView textView = new TextView(appContext);
            textView.setText(Integer.toString(retrieval.getBinNumber()));
            tableRow.addView(textView, 0);

            textView = new TextView(appContext);
            textView.setText(retrieval.getDescription());
            tableRow.addView(textView, 1);

            textView = new TextView(appContext);
            textView.setText(retrieval.getUnit());
            tableRow.addView(textView, 2);

            textView = new TextView(appContext);
            textView.setText(Integer.toString(retrieval.getQuantityNeeded()));
            tableRow.addView(textView, 3);

            EditText editView = new EditText(appContext);
            editView.setTag(retrieval.getItemId());
            editView.setInputType(InputType.TYPE_CLASS_NUMBER);
            editView.setText(Integer.toString(retrieval.getQuantityNeeded()));
            tableRow.addView(editView, 4);

            tableLayout.addView(tableRow);
        }
    }

    // get the quantity retrieved, convert list to JSONObject
    @Override
    public void onClick(View view) {
        // hide keyboard after pressing retrieve
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adjDetails = new ArrayList<AdjustmentDetails>();
        retrievalArray = new JSONArray();

        try {
            for (Retrieval retrieval : retrievals) {
                EditText editView = view.getRootView().findViewWithTag(retrieval.getItemId());
                int qty = Integer.parseInt(editView.getText().toString());

                // display error message if quantity retrieved is invalid
                if (qty<0 || qty>retrieval.getQuantityNeeded()) {
                    errorView.setVisibility(View.VISIBLE);
                    return;
                }

                // add to list of adjustments if quantity retrieved is less than needed
                if (qty<retrieval.getQuantityNeeded()) {
                    flag = true;

                    AdjustmentDetails adj = new AdjustmentDetails();
                    adj.setItemId(retrieval.getItemId());
                    adj.setDescription(retrieval.getDescription());
                    adj.setQtyAdjusted(-(retrieval.getQuantityNeeded()-qty));

                    adjDetails.add(adj);
                }

                JSONObject r = new JSONObject();
                r.put("ItemId", retrieval.getItemId());
                r.put("QuantityRetrieved", qty);
                retrievalArray.put(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // dialog alert for user to confirm
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_retrieve_title)
                .setMessage(R.string.dialog_retrieve_msg)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                retrieve();
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

    // convert list to JSONObject to send to server
    private void retrieve() {
        JSONObject data = new JSONObject();
        try {
            data.put("retrievals", retrievalArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Command cmd = new Command(this, "set","/StoreRetrieval/Retrieve", data);
        new AsyncToServer().execute(cmd);
    }
}
