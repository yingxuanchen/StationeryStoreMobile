package com.yingxuan.stationerystore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.session.User;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AsyncToServer.IServerResponse {

    ProgressBar pgbar;
    // TextView for displaying error messages
    TextView msgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnlogin = findViewById(R.id.btn_Login);
        msgView = findViewById(R.id.errorMsg);
        pgbar = findViewById(R.id.pbar);
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

        // clear error message
        msgView.setText("");

        // get user input of email and password
        EditText edtUID = findViewById(R.id.et_email);
        EditText edtPw = findViewById(R.id.et_password);
        String email = edtUID.getText().toString().trim();
        String password = edtPw.getText().toString().trim();

        if (email.equals("") || password.equals(""))
            msgView.setText(R.string.error_empty_inputs);
        else {
            JSONObject data = new JSONObject();
            try {
                data.put("email",email);
                data.put("password",password);
            } catch (Exception e){
                e.printStackTrace();
            }
            Command cmd = new Command(this, "get","/Login/Index", data);

            pgbar.setVisibility(View.VISIBLE);
            new AsyncToServer().execute(cmd);
        }
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        pgbar.setVisibility(View.GONE);

        if (jsonObj == null)
            return;

        try {
            String status = jsonObj.getString("status");

            if (status.equals("fail"))
                msgView.setText(R.string.error_wrong_inputs);

            else if (status.equals("ok")) {
                if (jsonObj.optJSONObject("employee") != null) {
                    JSONObject employee = jsonObj.getJSONObject("employee");

                    if (employee.getString("Role").equals("Head")) {
                        User.employeeId = employee.getString("Id");
                        User.name = employee.getString("Name");
                        User.role = employee.getString("Role");
                        User.departmentId = employee.getString("Department");
                        User.sessionId = jsonObj.getString("sessionId");
                    } else {
                        msgView.setText(R.string.error_unauthorised);
                    }
                }

                else {
                    JSONObject storeStaff = jsonObj.getJSONObject("storeStaff");
                    User.employeeId = storeStaff.getString("Id");
                    User.name = storeStaff.getString("Name");
                    User.role = storeStaff.getString("Role");
                    User.sessionId = jsonObj.getString("sessionId");

                    // change role name of store clerk so that can differentiate with normal employee
                    if (User.role.equals("Employee"))
                        User.role = "Clerk";
                }
            }

            else {
                msgView.setText(R.string.error_no_connection);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // go to main page if role is authorised
        switch (User.role) {
            case "Head":
            case "Clerk":
            case "Supervisor":
            case "Manager":
            Intent intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
            break;
        }
    }
}
