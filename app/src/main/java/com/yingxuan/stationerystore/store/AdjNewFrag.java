package com.yingxuan.stationerystore.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.List;

public class AdjNewFrag extends Fragment
        implements View.OnClickListener, AsyncToServer.IServerResponse,
        AdapterView.OnItemSelectedListener {

    private TextView errorMsgView;
    private Spinner spinner;
    private List<String> itemNames;
    private List<String> itemIds;
    private String selectedItemId;
    private int qty;
    private String reason;
    private String adjType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.adj_new_manual_frag, container, false);

        errorMsgView = view.findViewById(R.id.error_adj);
        errorMsgView.setVisibility(View.GONE);

        spinner = view.findViewById(R.id.item_list);
        spinner.setOnItemSelectedListener(this);

        Button submitBtn = view.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);

        // get items from server
        Command cmd = new Command(this, "get","/StoreInventoryAdjustment/New", null);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        try {
            String context = (String) jsonObj.get("context");

            // get items from server to display
            if (context.compareTo("get") == 0) {

                itemNames = new ArrayList<String>();
                itemIds = new ArrayList<String>();

                JSONArray items = jsonObj.getJSONArray("items");

                for (int i=0; i<items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String itemId = item.getString("Id");
                    String itemName = item.getString("Description");
                    itemIds.add(itemId);
                    itemNames.add(itemName);
                }

                // Create an ArrayAdapter using the list of items and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, itemNames);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            }
            else if (context.compareTo("set") == 0) {
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

        // at least 1 radio button must be checked
        RadioGroup radioGroup = view.getRootView().findViewById(R.id.radio_group);
        if (radioGroup.getCheckedRadioButtonId() != -1) {
            int selected = radioGroup.getCheckedRadioButtonId();
            RadioButton b = view.getRootView().findViewById(selected);
            adjType = b.getText().toString().toLowerCase();
        } else {
            errorMsgView.setText(R.string.error_adj_no_type);
            errorMsgView.setVisibility(View.VISIBLE);
            return;
        }

        // quantity cannot be 0 or empty
        EditText editView = view.getRootView().findViewById(R.id.qty_adjusted);
        String qtyString = editView.getText().toString().trim();
        if (qtyString.equals("")) {
            errorMsgView.setText(R.string.error_adj_qty_empty);
            errorMsgView.setVisibility(View.VISIBLE);
            return;
        }
        qty = Integer.parseInt(qtyString);
        if (qty == 0) {
            errorMsgView.setText(R.string.error_adj_qty_zero);
            errorMsgView.setVisibility(View.VISIBLE);
            return;
        }

        // reason cannot be empty
        editView = view.getRootView().findViewById(R.id.reason);
        reason = editView.getText().toString().trim();
        if (reason.equals("")) {
            errorMsgView.setText(R.string.error_adj_no_reason);
            errorMsgView.setVisibility(View.VISIBLE);
            return;
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
            data.put("sessionId", User.sessionId);
            data.put("itemId", selectedItemId);
            data.put("quantity", qty);
            data.put("reason", reason);
            data.put("adjustmentType", adjType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Command cmd = new Command(this, "set","/StoreInventoryAdjustment/Submit", data);
        new AsyncToServer().execute(cmd);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItemId = itemIds.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
