package com.yingxuan.stationerystore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.model.DisbursementDetails;
import com.yingxuan.stationerystore.model.SignView;
import com.yingxuan.stationerystore.model.StringAndBitmap;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity
        implements AsyncToServer.IServerResponse, View.OnClickListener{
    TextView tv_id;
    TextView tv_collectionPoint;
    TextView tv_status;
    TextView tv_department;
    TextView tv_repName;
    EditText et_remarks;
    TextView tv_remarks;
    Button confirmBtn;
    Button clearBtn;
    TableLayout tableLayout;
    SignView signView;
    ImageView iv_signature;

    List<Integer> IDList = new ArrayList<Integer>();
    List<DisbursementDetails> itemlist = new ArrayList<DisbursementDetails>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_collection);

        tv_id=findViewById(R.id.disbursement_id);
        tv_collectionPoint=findViewById(R.id.collection_point);
        tv_status=findViewById(R.id.status);
        tv_department=findViewById(R.id.department);
        tv_repName=findViewById(R.id.representative);
        et_remarks=findViewById(R.id.remarks);

        tv_remarks=findViewById(R.id.TV_remarks);

        confirmBtn=findViewById(R.id.confirmBtn);
        clearBtn=findViewById(R.id.clearSign);
        tableLayout=findViewById(R.id.table);
        signView=findViewById(R.id.sign_view);
        iv_signature=findViewById(R.id.IV_signature);

        clearBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        Bundle extras=getIntent().getExtras();
        if(extras==null)
            return;
        String id= extras.getString("id");
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Command cmd = new Command(this,"getDisbursementDetails","/StoreDisbursement/GetDisbursementDetailsById",jsonObj);
        new AsyncToServer().execute(cmd);
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        try {
            String context = (String) jsonObj.get("context");
            if (context.compareTo("getDisbursementDetails") == 0) {
                String id=jsonObj.getJSONObject("form").getString("Id");
                String department=jsonObj.getJSONObject("form").getString("Department");
                String collectionPoint=jsonObj.getJSONObject("form").getString("CollectionPoint");
                String status=jsonObj.getJSONObject("form").getString("Status");
                String repName=jsonObj.getJSONObject("form").getString("CollectedBy");
                String remarks=jsonObj.getJSONObject("form").getString("Remarks");
                String img=jsonObj.getJSONObject("form").getString("Img");

                tv_id.setText(id);
                tv_department.setText(department);
                tv_collectionPoint.setText(collectionPoint);
                tv_status.setText(status);
                tv_repName.setText(repName);
                et_remarks.setText(remarks);
                tv_remarks.setText(remarks);

                //check status to show different UI
                if(status.equals("Collected")) {
                    et_remarks.setVisibility(View.GONE);
                    signView.setVisibility(View.GONE);
                    clearBtn.setVisibility(View.GONE);
                    confirmBtn.setVisibility(View.GONE);
                    Bitmap bitmap =StringAndBitmap.stringToBitmap(img);
                    iv_signature.setImageBitmap(bitmap);

                }else{
                    tv_remarks.setVisibility(View.GONE);
                    iv_signature.setVisibility(View.GONE);
                }

                JSONArray itemArray = jsonObj.getJSONObject("form").getJSONArray("Itemlist");

                for (int i=0; i<itemArray.length(); i++) {
                    JSONObject item = itemArray.getJSONObject(i);
                    DisbursementDetails d = new DisbursementDetails();
                    d.setItemId(item.getString("ItemId"));
                    d.setItemName(item.getString("ItemName"));
                    d.setQtyRetrieved(item.getInt("QtyRetrieved"));
                    d.setQtyCollected(item.getInt("QtyCollected"));
                    itemlist.add(d);
                }


                for (int i=0; i<itemlist.size(); i++) {
                    DisbursementDetails d = new DisbursementDetails();
                    d=itemlist.get(i);
                    // Create a new table row.
                    TableRow tableRow = new TableRow(this);

                    // Set new table row layout parameters
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    tableRow.setLayoutParams(layoutParams);

                    // Add a TextView in each column
                    TextView textView = new TextView(this);
                    textView.setText(d.getItemId());

                    TableRow.LayoutParams textLayoutParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1);
                    textLayoutParams1.weight=1;
                    textLayoutParams1.width=0;
                    textLayoutParams1.gravity= Gravity.RIGHT;
                    textLayoutParams1.leftMargin=5;
                    tableRow.addView(textView, 0,textLayoutParams1);

                    textView = new TextView(this);
                    textView.setText(d.getItemName());
                    TableRow.LayoutParams textLayoutParams2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,2);
                    textLayoutParams2.weight=3;
                    textLayoutParams2.width=0;
                    textLayoutParams2.rightMargin=0;
                    tableRow.addView(textView, 1,textLayoutParams2);

                    textView = new TextView(this);
                    textView.setText(Integer.toString(d.getQtyRetrieved()));
                    tableRow.addView(textView, 2);

                    if(status.equals("Collected")) {
                        textView = new TextView(this);
                        textView.setText(Integer.toString(d.getQtyCollected()));
                        tableRow.addView(textView, 3,textLayoutParams1);
                    }else{
                        EditText editView = new EditText(this);
                        editView.setText(Integer.toString(d.getQtyRetrieved()));
                        editView.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editView.setId(View.generateViewId());
                        IDList.add(editView.getId());
                        tableRow.addView(editView, 3,textLayoutParams1);
                    }
                    tableLayout.addView(tableRow);
                }
            }
            if (context.compareTo("confirmCollection") == 0) {
                Intent intent = new Intent(this, FirstActivity.class);
                Bundle bundle= new Bundle();
                bundle.putString("flag", "disbursementfrag");
                intent.putExtras(bundle);
                startActivity(intent);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clearSign:
                signView.clear();
                break;
            case R.id.confirmBtn:

                JSONArray jsonArray = new JSONArray();
                for (int i=0; i<itemlist.size(); i++) {
                    DisbursementDetails d;
                    d = itemlist.get(i);
                    EditText item = findViewById(IDList.get(i));
                    String qtyString = item.getText().toString().trim();
                    if (qtyString.equals("")) {
                        Toast.makeText(this,R.string.error_no_reason,Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (qtyString.length() > 4) {
                        Toast.makeText(this,R.string.error_qty_big,Toast.LENGTH_LONG).show();
                        return;
                    }

                    int qty=Integer.parseInt(qtyString);
                    if(qty>d.getQtyRetrieved()){
                        Toast.makeText(this,"Quantity collected cannot be more than delivered",Toast.LENGTH_LONG).show();
                        return;
                    }

                    JSONObject JSONitem = new JSONObject();
                    try{
                        JSONitem.put("itemId",d.getItemId());
                        JSONitem.put("qtyRetrieved",d.getQtyRetrieved());
                        JSONitem.put("qtyCollected",qty);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(JSONitem);
                }

                String bitmap = StringAndBitmap.bitmapToString(signView.getBitmap());
                String id=tv_id.getText().toString();
                String remarks=et_remarks.getText().toString();

                JSONObject jsonObj = new JSONObject();

                try {
                    jsonObj.put("id", id);
                    jsonObj.put("remarks", remarks);
                    jsonObj.put("bitmap", bitmap);
                    jsonObj.put("itemlist", jsonArray);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Command cmd = new Command(this,"confirmCollection","/StoreDisbursement/SubmitDisbursement",jsonObj);
                new AsyncToServer().execute(cmd);

                break;
        }
    }


}
