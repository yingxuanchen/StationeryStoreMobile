package com.yingxuan.stationerystore.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.model.Retrieval;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.adj_new_frag, container, false);

        errorMsgView = view.findViewById(R.id.reason_empty);
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

            if (context.compareTo("get") == 0) {
                // Convert JSON to an ArrayList of item ids and item names
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

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // get the adjustment details, convert to JSONObject to send to server
    @Override
    public void onClick(View view) {
        JSONObject data = new JSONObject();
        try {
            // at least 1 radio button must be checked
            RadioGroup radioGroup = view.getRootView().findViewById(R.id.radio_group);
            String adjType;
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
            if (qtyString.equals("") || qtyString == null) {
                errorMsgView.setText(getString(R.string.error_adj_qty_empty));
                errorMsgView.setVisibility(View.VISIBLE);
                return;
            }
            int qty = Integer.parseInt(qtyString);
            if (qty == 0) {
                errorMsgView.setText(R.string.error_adj_qty_zero);
                errorMsgView.setVisibility(View.VISIBLE);
                return;
            }

            // reason cannot be empty
            editView = view.getRootView().findViewById(R.id.reason);
            String reason = editView.getText().toString();
            if (reason == null || reason.trim().equals("")) {
                errorMsgView.setText(R.string.error_adj_no_reason);
                errorMsgView.setVisibility(View.VISIBLE);
                return;
            }

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
