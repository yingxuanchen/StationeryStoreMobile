package com.yingxuan.stationerystore.connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {

    private static String ip = "192.168.1.3";
    private static String database = "StationeryStore";
    private static String username = "cyx";
    private static String password = "password";

    @SuppressLint("NewApi")
    public static Connection getConn() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String connURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connURL = "jdbc:jtds:sqlserver://" + ip + "/" + database + ";user=" + username + ";password=" + password + ";";
            conn = DriverManager.getConnection(connURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        } catch (ClassNotFoundException ce)
        {
            Log.e("error here 2 : ", ce.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }

        return conn;
    }
}
