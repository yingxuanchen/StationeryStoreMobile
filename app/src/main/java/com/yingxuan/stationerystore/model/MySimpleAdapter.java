package com.yingxuan.stationerystore.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yingxuan.stationerystore.CollectionActivity;
import com.yingxuan.stationerystore.R;

import java.util.List;
import java.util.Map;

public class MySimpleAdapter extends SimpleAdapter
        implements View.OnClickListener {

    private Context mContext;
    private List<? extends Map<String, ?>> mdata;
    private int layout;
    private int mPosition;
    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext=context;
        mdata=data;
        layout=resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        mPosition=position;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.TV_ID=(TextView)convertView.findViewById(R.id.disbursement_id);
            viewHolder.TV_Date=(TextView)convertView.findViewById(R.id.date);
            viewHolder.TV_Department=(TextView)convertView.findViewById(R.id.department);
            viewHolder.TV_Status=(TextView)convertView.findViewById(R.id.status);
            viewHolder.btn_View=(Button) convertView.findViewById(R.id.view_button);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.TV_ID.setText(mdata.get(position).get("Id").toString());
        viewHolder.TV_Date.setText(mdata.get(position).get("DateCreated").toString());
        viewHolder.TV_Department.setText(mdata.get(position).get("DepartmentId").toString());
        viewHolder.TV_Status.setText(mdata.get(position).get("Status").toString());

        Button btn = (Button) convertView.findViewById(R.id.view_button);
        btn.setTag(mdata.get(position).get("Id").toString());
        btn.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(mContext,"click "+ v.getTag(),Toast.LENGTH_SHORT).show();
        String id=v.getTag().toString();

        Intent intent = new Intent(mContext, CollectionActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("id", id);
        intent.putExtras(bundle);
        ((Activity) mContext).startActivityForResult(intent,1);
    }


    static class ViewHolder {
        TextView TV_ID;
        TextView TV_Date;
        TextView TV_Department;
        TextView TV_Status;
        Button btn_View;
    }


}

