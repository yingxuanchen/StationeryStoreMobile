package com.yingxuan.stationerystore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailView = findViewById(R.id.emailView);
                EditText pwdView = findViewById(R.id.pwdView);

                String email = emailView.getText().toString();
                String pwd = pwdView.getText().toString();
            }
        });
    }

}
