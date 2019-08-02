package com.yingxuan.stationerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.yingxuan.stationerystore.department.ApproveFrag;
import com.yingxuan.stationerystore.department.DelegateFrag;
import com.yingxuan.stationerystore.department.RepFrag;
import com.yingxuan.stationerystore.session.User;
import com.yingxuan.stationerystore.store.RetrievalFrag;

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
        nv = findViewById(R.id.nv);

        dl.addDrawerListener(t);
        t.syncState();

        // true to show the user that selecting home will return one level up rather than to the top level of the app.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        // set first fragment to use for each role
        Fragment frag = null;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        switch(User.role) {
            case "Head":
                frag = new ApproveFrag();
                break;
            case "Clerk":
            case "Supervisor":
            case "Manager":
                frag = new RetrievalFrag();
                break;
        }
        trans.replace(R.id.frag, frag);
        trans.commit();

        // set the fragment to use for each menu item
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                Fragment frag = null;
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction trans = fm.beginTransaction();

                switch(id) {
                    case R.id.approve:
                        frag = new ApproveFrag();
                        break;
                    case R.id.rep:
                        frag = new RepFrag();
                        break;
                    case R.id.delegate:
                        frag = new DelegateFrag();
                        break;
                    case R.id.retrieve:
                        frag = new RetrievalFrag();
                        break;
                    case R.id.disbursement:
                        frag = new RetrievalFrag();
                        break;
                    case R.id.adjustment:
                        frag = new RetrievalFrag();
                        break;
                    case R.id.logout:
                        User.name = "";
                        User.employeeId = "";
                        User.role = "";
                        User.departmentId = "";
                        User.sessionId = "";
                        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                        // prevent user from being able to press back to access previous session
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                }
                trans.replace(R.id.frag, frag);
                trans.commit();

                // close menu once something is selected
                dl.closeDrawers();
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
