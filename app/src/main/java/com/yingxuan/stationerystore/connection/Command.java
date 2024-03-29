package com.yingxuan.stationerystore.connection;

import org.json.JSONObject;

public class Command {
    private static String root = "http://10.0.2.2";

    protected AsyncToServer.IServerResponse callback;
    protected String context;
    protected String endPt;
    protected JSONObject data;

    public Command(AsyncToServer.IServerResponse callback,
            String context, String endPt, JSONObject data)
    {
        this.callback = callback;
        this.context = context;
        this.endPt = root + endPt;
        this.data = data;
    }
}
