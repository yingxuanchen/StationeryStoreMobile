package com.yingxuan.stationerystore.department;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yingxuan.stationerystore.MainActivity;
import com.yingxuan.stationerystore.R;
import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;

import org.json.JSONException;
import org.json.JSONObject;

public class DelegateCheckFrag extends Fragment implements AsyncToServer.IServerResponse {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Command cmd = new Command(this, "get","/DeptDelegation/Check", null);
        new AsyncToServer().execute(cmd);
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            // prevent user from being able to press back to access previous session
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        try {
            // use different Fragment depending on whether there is existing delegation
            Fragment frag = null;
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction trans = fm.beginTransaction();

            String delegationStatus = jsonObj.getString("delegation");
            if (delegationStatus.equals("No Delegation"))
                frag = new DelegateFrag();
            else if (delegationStatus.equals("Delegated"))
                frag = new DelegateCancelFrag();

            trans.replace(R.id.frag, frag);
            trans.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
