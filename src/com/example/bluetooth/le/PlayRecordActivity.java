package com.example.bluetooth.le;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;

import data.NikeDatabase;


public class PlayRecordActivity extends ListActivity {

    private String[] records = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //***setContentView(R.layout.activity_play_record);

        records = getRecords();
        if (records.length == 0)
            Toast.makeText(this, "You have not any record", Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                records
        );
        setListAdapter(arrayAdapter);

    }

    private String[] getRecords() {
        NikeDatabase database = null;
        try {
            database = new NikeDatabase(this, "NikeSensor.db");
            return database.getDataNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        String recordName = records[position];
        Intent intent = new Intent(this, RecordChartActivity.class);
        intent.putExtra("record", recordName);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	//***getMenuInflater().inflate(R.menu.menu_play_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      //***if (id == R.id.action_settings) {
      //***    return true;
      //***}

        return super.onOptionsItemSelected(item);
    }
}
