package com.yingxuan.stationerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class FirstActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        dl = findViewById(R.id.dl);
        t = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);

        dl.addDrawerListener(t);
        t.syncState();

        // true to show the user that selecting home will return one level up rather than to the top level of the app.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);

        // set user's name on menu header
        View headerView =  nv.getHeaderView(0);
        TextView nameView = headerView.findViewById(R.id.nameView);
        nameView.setText(User.name);

        // set menu items based on role
        switch (User.role) {
            case "Head": nv.inflateMenu(R.menu.dept_menu); break;
            case "Clerk": nv.inflateMenu(R.menu.store_clerk_menu); break;
            case "Supervisor":
            case "Manager": nv.inflateMenu(R.menu.store_menu); break;
        }

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.retrieve:
                        break;
                    case R.id.disbursement:
                        break;
                    case R.id.adjustment:
                        break;
                    case R.id.logout:
                        User.name = "";
                        User.employeeId = "";
                        User.role = "";
                        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                        // prevent user from being able to press back to access previous session
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}
