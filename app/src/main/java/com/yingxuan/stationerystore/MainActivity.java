package com.yingxuan.stationerystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.yingxuan.stationerystore.ConnectionClass;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnlogin = findViewById(R.id.btn_Login);
        ProgressBar pgbar = findViewById(R.id.pbar);
        pgbar.setVisibility(View.GONE);

        btnlogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        EditText edtUID = findViewById(R.id.et_email);
        EditText edtPw = findViewById(R.id.et_password);
        String email = edtUID.getText().toString();
        String pwd = edtPw.getText().toString();

        new DoLogin(MainActivity.this).execute(email, pwd);
    }

    public class DoLogin extends AsyncTask<String,Void,String> {
        private WeakReference<MainActivity> parent = null;

        public DoLogin(MainActivity parent) {
            this.parent = new WeakReference<>(parent);
        }

        @Override
        protected void onPreExecute() {
            this.parent.get().findViewById(R.id.pbar).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String msg = "";
            if (params[0].trim().equals("") || params[1].trim().equals(""))
                msg = "Please enter Email and Password.";
            else {
                try {
                    Connection con = ConnectionClass.getConn();
                    if (con == null) {
                        msg = "Error in connection with server";
                    } else {
                        String query = "SELECT * FROM Employee WHERE email='" + params[0].trim() + "' AND pwd='" + params[1].trim() + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if (!rs.next())
                            msg = "Email or Password is wrong.";
                        else
                            msg = "successful";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return msg;
        }

        @Override
        protected void onPostExecute (String msg) {
            MainActivity parent = this.parent.get();
            ProgressBar pgbar = parent.findViewById(R.id.pbar);
            pgbar.setVisibility(View.GONE);

            if (msg != "") {
                TextView msgView = parent.findViewById(R.id.errorMsg);
                msgView.setText(msg);
            }
        }
    }
}
