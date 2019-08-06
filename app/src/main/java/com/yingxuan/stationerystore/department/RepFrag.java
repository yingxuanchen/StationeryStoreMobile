package com.yingxuan.stationerystore.department;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONObject;

public class RepFrag extends Fragment implements AsyncToServer.IServerResponse{
    FragmentActivity listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assignrep_frag, container, false);

        JSONObject data = new JSONObject();
        try {
            data.put("departmentId",User.departmentId);
        } catch (Exception e){
            e.printStackTrace();
        }

        Command cmd = new Command(this, "get","/DeptAssignRep/Index", data);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        System.out.println(jsonObj);
    }
}
