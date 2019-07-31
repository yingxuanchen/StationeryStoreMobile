package com.yingxuan.stationerystore;

import org.json.JSONObject;

public class Command {
    private static String root = "http://fec01a8c.ngrok.io";

    protected AsyncToServer.IServerResponse callback;
    protected String context;
    protected String endPt;
    protected JSONObject data;

    Command(AsyncToServer.IServerResponse callback,
            String context, String endPt, JSONObject data)
    {
        this.callback = callback;
        this.context = context;
        this.endPt = root + endPt;
        this.data = data;
    }
}
