package com.yingxuan.stationerystore;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        // hide keyboard after pressing login
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check for user in database
        EditText edtUID = findViewById(R.id.et_email);
        EditText edtPw = findViewById(R.id.et_password);
        String email = edtUID.getText().toString();
        String pwd = edtPw.getText().toString();

        new DoLogin(MainActivity.this).execute(email, pwd);
    }

    public class DoLogin extends AsyncTask<String,Void,String> {
        private WeakReference<MainActivity> parent;

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
            String email = params[0].trim();
            String pwd = params[1].trim();

            if (email.equals("") || pwd.equals(""))
                msg = "Please enter Email and Password";
            else {
                try {
                    Connection con = ConnectionClass.getConn();
                    if (con == null) {
                        msg = "Error in connection with server";
                    } else {
                        String query1 = "SELECT * FROM Employee WHERE email='" + email + "' AND pwd='" + pwd + "'";
                        String query2 = "SELECT * FROM StoreStaff WHERE email='" + email + "' AND pwd='" + pwd + "'";
                        Statement stmt1 = con.createStatement();
                        ResultSet rs1 = stmt1.executeQuery(query1);
                        Statement stmt2 = con.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(query2);

                        if (rs1.next()) {
                            User.employeeId = rs1.getString("id");
                            User.name = rs1.getString("name");
                            User.role = rs1.getString("role");
                            // Department Employee & Rep & TempHead are not authorised to use mobile app
                            if (User.role.equals("Employee") || User.role.equals("Rep") || User.role.equals("TempHead"))
                                msg = "You are not authorised to use mobile app";
                        } else if (rs2.next()) {
                            User.employeeId = rs2.getString("id");
                            User.name = rs2.getString("name");
                            User.role = rs2.getString("role");
                            // change role name of store clerk so that can differentiate with normal employee
                            if (User.role.equals("Employee"))
                                User.role = "Clerk";
                        } else {
                            msg = "Email or Password is wrong";
                        }
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

            // display error message
            if (msg != "") {
                TextView msgView = parent.findViewById(R.id.errorMsg);
                msgView.setText(msg);
            }

            // go to main page if role is authorised
            switch (User.role) {
                case "Head":
                case "Clerk":
                case "Supervisor":
                case "Manager":
                    Intent intent = new Intent(parent, FirstActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
