package com.example.bluetooth.le;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.sql.SQLException;

import data.NikeDataGetter;
import data.NikeDataLoader;
import data.NikeDataPlayer;
import data.NikeDatabase;
import data.NotFoundRecordException;
import data.PlayTimeListener;
import gui.FootPressureView;
import sensor.Direction;


public class RecordChartActivity extends Activity {

    private NikeDataPlayer dataPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //***setContentView(R.layout.activity_record_chart);

        Intent intent = getIntent();
        String recordName = intent.getStringExtra("record");
        try {
            NikeDatabase database = new NikeDatabase(this, "NikeSensor.db");
            NikeDataLoader dataLoader = new NikeDataLoader(database);
            dataLoader.loadData(recordName);
            dataPlayer = new NikeDataPlayer(dataLoader);
        } catch (Exception e) {
            Log.e("DB", e.toString());
        }

        NikeDataGetter leftDataGetter = new NikeDataGetter(Direction.LEFT);
        NikeDataGetter rightDataGetter = new NikeDataGetter(Direction.RIGHT);
        dataPlayer.setDataGetter(leftDataGetter, rightDataGetter);

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart_layout);

        FootPressureView leftView = new FootPressureView(this, leftDataGetter);
        FootPressureView rightView = new FootPressureView(this, rightDataGetter);

        leftView.setLayoutParams(
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        rightView.setLayoutParams(
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        layout.addView(leftView);
        layout.addView(rightView);

        setPlayTimeBar();

        Button startButton = null;//***(Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataPlayer.getCurrentTime() >= dataPlayer.getLength())
                    dataPlayer.setCurrentTime(0);
                dataPlayer.start();
            }
        });
        Button stopButton = null;//***(Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPlayer.stop();
            }
        });
    }

    private void setPlayTimeBar() {
        final SeekBar playTimeBar = null;//***(SeekBar) findViewById(R.id.playTimeBar);
        playTimeBar.setMax(dataPlayer.getLength());
        playTimeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                dataPlayer.setCurrentTime(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                dataPlayer.stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        dataPlayer.addPlayTimeListener(new PlayTimeListener() {
            @Override
            public void tick(int time) {
                playTimeBar.setProgress(time);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	//***getMenuInflater().inflate(R.menu.menu_record_chart, menu);
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
