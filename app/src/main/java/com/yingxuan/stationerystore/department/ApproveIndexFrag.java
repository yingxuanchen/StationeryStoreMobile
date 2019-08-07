package com.yingxuan.stationerystore.department;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.yingxuan.stationerystore.model.RequisitionForm;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApproveIndexFrag extends Fragment implements AdapterView.OnItemClickListener, AsyncToServer.IServerResponse {

    private Context appContext = null;
    private List<RequisitionForm> reqFormList;
    private RequisitionForm reqForm;
    private User user;
    private TextView emptyMsgView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.approve_index_frag, container, false);

        emptyMsgView = view.findViewById(R.id.apvlist_empty);
        emptyMsgView.setVisibility(View.GONE);

        Command cmd = new Command(this, "get", "/deptapproverequisitions/approverequisition", null);
        new AsyncToServer().execute(cmd);

        return view;


    }


    @Override
    public void onServerResponse(JSONObject jsonObj) {

        if (jsonObj == null)
            return;

        // Convert JSON to an ArrayList of requisitions
        reqFormList = new ArrayList<RequisitionForm>();

        try {

            JSONArray reqIdArray = jsonObj.getJSONArray("rqIdList");
            JSONArray empNameArray = jsonObj.getJSONArray("empNameList");
            JSONArray dates = jsonObj.getJSONArray("dates");

            if (reqIdArray.length() == 0)
                emptyMsgView.setVisibility(View.VISIBLE);

            for (int i = 0; i < reqIdArray.length(); i++) {
                String reqId = reqIdArray.getString(i);
                String empName = empNameArray.getString(i);
                String date = dates.getString(i);

                reqForm = new RequisitionForm(reqId, date, empName);
                reqFormList.add(reqForm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //load data into list view
        ListView listView = getView().findViewById(R.id.apvlist_index_list);
        listView.setAdapter(new SimpleAdapter(getContext(), reqFormList, R.layout.approve_index_row,
                new String[]{"id", "empName", "dateSubmitted"},
                new int[]{R.id.appindex_reqID, R.id.appindex_empName, R.id.appindex_date_submitted}));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        RequisitionForm requisitionForm = (RequisitionForm) parent.getItemAtPosition(position);

        Bundle bundle = new Bundle();
        bundle.putString("id", (String) requisitionForm.get("id"));
        Fragment frag = new ApproveFrag();
        frag.setArguments(bundle);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.frag, frag);
        trans.addToBackStack(null);
        trans.commit();
    }
}

