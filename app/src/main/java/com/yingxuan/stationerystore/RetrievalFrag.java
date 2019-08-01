package com.yingxuan.stationerystore;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yingxuan.stationerystore.connection.AsyncToServer;
import com.yingxuan.stationerystore.connection.Command;
import com.yingxuan.stationerystore.model.Retrieval;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RetrievalFrag extends Fragment
        implements View.OnClickListener, AsyncToServer.IServerResponse {

    private Context appContext = null;
    private List<Retrieval> retrievalForm;
    private Retrieval retrieval;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.retrieval_frag, container, false);

        Button retrieveBtn = view.findViewById(R.id.retrieveBtn);
        retrieveBtn.setOnClickListener(this);

        // get retrieval form from server
        Command cmd = new Command(this, "get","/Api/GetRetrieval", null);
        new AsyncToServer().execute(cmd);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject jsonObj) {
        if (jsonObj == null)
            return;

        try {
            String context = (String) jsonObj.get("context");

            if (context.compareTo("get") == 0)
            {
                // Convert JSON to an ArrayList of retrieval items
                retrievalForm = new ArrayList<Retrieval>();
                JSONArray itemArray = jsonObj.getJSONArray("retrievalForm");

                for (int i=0; i<itemArray.length(); i++) {
                    JSONObject item = itemArray.getJSONObject(i);

                    retrieval = new Retrieval();
                    retrieval.setItemId(item.getString("ItemId"));
                    retrieval.setDescription(item.getString("Description"));
                    retrieval.setBinNumber(item.getInt("BinNumber"));
                    retrieval.setUnit(item.getString("Unit"));
                    retrieval.setQuantityNeeded(item.getInt("QuantityNeeded"));

                    retrievalForm.add(retrieval);
                }
            }
            else if (context.compareTo("set") == 0)
            {
                //Todo
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        appContext = getActivity().getApplicationContext();

        // add items into TableLayout row by row
        TableLayout tableLayout = getView().findViewById(R.id.retrieve_table);

        for (int i=0; i<retrievalForm.size(); i++) {
            retrieval = retrievalForm.get(i);

            // Create a new table row.
            TableRow tableRow = new TableRow(appContext);

            // Set new table row layout parameters
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(layoutParams);

            // Add a TextView in each column
            TextView textView = new TextView(appContext);
            textView.setText(Integer.toString(retrieval.getBinNumber()));
            tableRow.addView(textView, 0);

            textView = new TextView(appContext);
            textView.setText(retrieval.getDescription());
            tableRow.addView(textView, 1);

            textView = new TextView(appContext);
            textView.setText(retrieval.getUnit());
            tableRow.addView(textView, 2);

            textView = new TextView(appContext);
            textView.setText(Integer.toString(retrieval.getQuantityNeeded()));
            tableRow.addView(textView, 3);

            EditText editView = new EditText(appContext);
            editView.setText(Integer.toString(retrieval.getQuantityNeeded()));
            tableRow.addView(editView, 4);

            tableLayout.addView(tableRow);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
