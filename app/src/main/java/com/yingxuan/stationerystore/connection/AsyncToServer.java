package com.yingxuan.stationerystore.connection;

import android.os.AsyncTask;

import com.yingxuan.stationerystore.session.User;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncToServer extends AsyncTask<Command, Void, JSONObject> {
    IServerResponse callback;

    @Override
    protected JSONObject doInBackground(Command... cmds) {
        Command cmd = cmds[0];
        this.callback = cmd.callback;

        JSONObject jsonObj = null;
        StringBuilder response = new StringBuilder();
        HttpURLConnection conn = null;

        try {
            URL url = new URL(cmd.endPt);
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Cookie","ASP.NET_SessionId=" + User.sessionId);
            conn.setConnectTimeout(20000);

            // send data
            if (cmd.data != null) {
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                DataOutputStream outstream = new DataOutputStream(conn.getOutputStream());
                outstream.writeBytes(cmd.data.toString());
                outstream.flush();
                outstream.close();
            }

            // receive response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null; ) {
                response.append(line).append('\n');
            }

            try {
                jsonObj = new JSONObject(response.toString());
                jsonObj.put("context", cmd.context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return jsonObj;
        } finally {
            conn.disconnect();
        }

        return jsonObj;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObj) {
//        if (jsonObj != null)
            this.callback.onServerResponse(jsonObj);
    }

    public interface IServerResponse {
        void onServerResponse(JSONObject jsonObj);
    }
}
