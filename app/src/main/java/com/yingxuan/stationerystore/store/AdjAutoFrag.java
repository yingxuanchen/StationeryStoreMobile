package com.yingxuan.stationerystore.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.yingxuan.stationerystore.session.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdjAutoFrag extends Fragment
        implements View.OnClickListener, AsyncToServer.IServerResponse {

    private TextView errorMsgView;
    private ArrayList<AdjustmentDetails> adjDetails;
    private JSONArray adjArray;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.adj_new_auto_frag, container, false);

        errorMsgView = view.findViewById(R.id.error_adj);
        errorMsgView.setVisibility(View.GONE);

        Button submitBtn = view.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);

        // adj details gotten from retrieval page
        Bundle bundle = getArguments();
        if (bundle != null) {
            adjDetails = (ArrayList<AdjustmentDetails>) bundle.getSerializable("adjDetails");
            // remove data to prevent future "Parcel: unable to marshal value" exception
            getArguments().remove("adjDetails");
        }

        // add a View for each item in the list
        ViewGroup linearLayout = view.findViewById(R.id.adj_new_list);
        for (int i=0; i<adjDetails.size(); i++) {
            AdjustmentDetails adjItem = adjDetails.get(i);

            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.adj_new, null);
            linearLayout.addView(itemView);

            // replace Spinner with TextView
            View spinnerView = itemView.findViewById(R.id.item_list);
            ViewGroup parent = (ViewGroup) spinnerView.getParent();
            int index = parent.indexOfChild(spinnerView);
            parent.removeView(spinnerView);
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,60);
            textView.setLayoutParams(params);
            textView.setText(adjItem.getDescription());
            parent.addView(textView, index);

            // check "Out" RadioButton and disable RadioButtons
            RadioButton outBtn = itemView.findViewById(R.id.qty_out);
            outBtn.setChecked(true);
            outBtn.setEnabled(false);
            RadioButton inBtn = itemView.findViewById(R.id.qty_in);
            inBtn.setEnabled(false);

            // auto-fill quantity, tag the quantity view
            TextView qtyView = itemView.findViewById(R.id.qty_adjusted);
            qtyView.setText(Integer.toString(-adjItem.getQtyAdjusted()));
            qtyView.setTag(adjItem.getItemId() + "_qty");

            // tag the reason view
            TextView reasonView = itemView.findViewById(R.id.reason);
            reasonView.setTag(adjItem.getItemId() + "_reason");
        }

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        try {
            String status = jsonObj.getString("status");

            // after successfully submitting adjustment voucher, redirect to Adjustment Index Fragment
            if (status.equals("ok")) {
                Toast.makeText(getContext(), R.string.toast_adj,
                        Toast.LENGTH_LONG).show();

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction trans = fm.beginTransaction();
                Fragment frag = new AdjIndexFrag();
                trans.replace(R.id.frag, frag);
                trans.commit();
            }
            // if out quantity is more than quantity in warehouse, display error message
            else if (status.equals("Invalid quantity")) {
                Toast.makeText(getContext(), R.string.error_adj_qty_invalid,
                        Toast.LENGTH_LONG).show();

                errorMsgView.setText(R.string.error_adj_qty_invalid);
                errorMsgView.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // get the adjustment details, convert to JSONObject
    @Override
    public void onClick(View view) {
        // hide keyboard after pressing submit
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adjArray = new JSONArray();
        try {
            // validate the inputs per item
            for (int i=0; i<adjDetails.size(); i++) {
                AdjustmentDetails adj = adjDetails.get(i);

                // quantity cannot be empty
                EditText editView = view.getRootView().findViewWithTag(adj.getItemId() + "_qty");
                String qtyString = editView.getText().toString().trim();
                if (qtyString.equals("")) {
                    errorMsgView.setText(R.string.error_adj_qty_empty);
                    errorMsgView.setVisibility(View.VISIBLE);
                    return;
                }
                int qty = Integer.parseInt(qtyString);
                // if quantity is 0, don't need to add to voucher
                if (qty == 0)
                    continue;

                // reason cannot be empty
                editView = view.getRootView().findViewWithTag(adj.getItemId() + "_reason");
                String reason = editView.getText().toString().trim();
                if (reason.equals("")) {
                    errorMsgView.setText(R.string.error_adj_no_reason);
                    errorMsgView.setVisibility(View.VISIBLE);
                    return;
                }

                JSONObject a = new JSONObject();
                a.put("ItemId", adj.getItemId());
                a.put("QtyAdjusted", -qty);
                a.put("Reason", reason);
                adjArray.put(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // dialog alert for user to confirm
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_adj_title)
                .setMessage(R.string.dialog_adj_msg)
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

    // convert adjustment details to JSONObject to send to server
    private void submit() {
        JSONObject data = new JSONObject();
        try {
            data.put("adjList", adjArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Command cmd = new Command(this, "set",
                "/StoreInventoryAdjustment/SubmitMultiple", data);
        new AsyncToServer().execute(cmd);
    }
}
