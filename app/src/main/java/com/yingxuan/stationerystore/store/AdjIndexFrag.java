package com.yingxuan.stationerystore.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.model.Adjustment;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdjIndexFrag extends Fragment
        implements View.OnClickListener, AsyncToServer.IServerResponse,
        AdapterView.OnItemClickListener {

    private List<Adjustment> list;
    private TextView emptyMsgView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.adj_index_frag, container, false);

        emptyMsgView = view.findViewById(R.id.adj_empty);
        emptyMsgView.setVisibility(View.GONE);

        Button adjNewBtn = view.findViewById(R.id.adj_new_btn);
        adjNewBtn.setOnClickListener(this);

        // get adjustment vouchers from server
        JSONObject data = new JSONObject();
        try {
            data.put("sessionId", User.sessionId);
        } catch (Exception e){
            e.printStackTrace();
        }
        Command cmd = new Command(this, "get","/StoreInventoryAdjustment/Index", data);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        // Convert JSON to an ArrayList of adjustment vouchers
        list = new ArrayList<Adjustment>();
        try {
            JSONArray vouchers = jsonObj.getJSONArray("vouchers");
            JSONArray dates = jsonObj.getJSONArray("dates");
            Adjustment adj;

            if (vouchers.length() == 0)
                emptyMsgView.setVisibility(View.VISIBLE);

            for (int i=0; i<vouchers.length(); i++) {
                JSONObject v = vouchers.getJSONObject(i);
                String id = v.getString("Id");
                String name = v.getString("EmployeeName");
                String status = v.getString("Status");
                double totalValue = v.getDouble("TotalValue");

                String date = dates.getString(i);

                adj = new Adjustment(id, date, name, status, totalValue);
                list.add(adj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // load data into list view
        ListView listView = getView().findViewById(R.id.adj_index_list);
        listView.setAdapter(new SimpleAdapter(getContext(), list, R.layout.adj_index_row,
                new String[] {"id", "dateString", "submittedBy", "status"},
                new int[] {R.id.voucher_no, R.id.date_submitted, R.id.submitted_by, R.id.status}));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Adjustment adj = (Adjustment) parent.getItemAtPosition(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable("adj", adj);
        Fragment frag = new AdjDetailsFrag();
        frag.setArguments(bundle);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.frag, frag);
        trans.addToBackStack(null);
        trans.commit();
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        Fragment frag = new AdjNewFrag();
        trans.replace(R.id.frag, frag);
        trans.addToBackStack(null);
        trans.commit();
    }
}
