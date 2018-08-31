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

import java.sql.SQLException;

import data.NikeDataLogger;
import data.NikeDatabase;


public class MainActivity extends ListActivity {

    private final static String[] ITEMS = {
            "Sensor Monitor & Record",
            "Play Record"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //***setContentView(R.layout.activity_main);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                ITEMS
        );
        setListAdapter(arrayAdapter);

        createDataTable();
    }

    private void createDataTable() {
        NikeDatabase database = null;
        try {
            database = new NikeDatabase(this, "NikeSensor.db");
            database.createDataTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        switch (position) {
            case 0:
                startActivity(new Intent(this, DeviceScanActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, PlayRecordActivity.class));
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	//***getMenuInflater().inflate(R.menu.menu_main, menu);
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
