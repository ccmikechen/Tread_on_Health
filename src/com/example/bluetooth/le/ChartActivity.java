package com.example.bluetooth.le;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import data.DataGetterInterface;
import gui.FootPressureView;


public class ChartActivity extends Activity {
	private final String MSG0 = "訓練！預備開始";
	private final String MSG1 = "好了～放鬆一下，請你坐下吧！";
	private final String MSG2 = "現在，起來走兩圈吧～";
	private final String MSG3 = "現在，立正！站好！";
	private final String MSG4 = "然後，就跑步位置，預備～跑！";
	private final String MSG5 = "好的！";
	
	private int state = 0;
	private Handler mHandler = new Handler();
	private TextView msg2User;
	
	private final Runnable stateChange = new Runnable() {
	    public void run() {
	    	switch(++state) {
		    	case 1:
		    		msg2User.setText(MSG1);
		    		//runTimer();
		    		break;
		    	case 2:
		    		msg2User.setText(MSG2);
		    		//runTimer();
		    		break;
		    	case 3:
		    		msg2User.setText(MSG3);
		    		//runTimer();
		    		break;
		    	case 4:
		    		msg2User.setText(MSG4);
		    		//runTimer();
		    		break;
		    	case 5:
		    		msg2User.setText(MSG5);
		    		//mHandler.postDelayed(stateChange, 5000);
		    		break;
		    	case 6:
		    		ChartActivity.this.finish();
		    		break;
	    	}
	    }
	};
	
	private void runTimer() {
		mHandler.postDelayed(stateChange, 8000);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        LinearLayout layout = (LinearLayout) findViewById(R.id.sensor_layout);
        layout.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		mHandler.postDelayed(stateChange, 200);
			};
        });

        FootPressureView leftView = new FootPressureView(this, DataGetterManager.getLeftDataGetter());
        FootPressureView rightView = new FootPressureView(this, DataGetterManager.getRightDataGetter());

        leftView.setLayoutParams(
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        rightView.setLayoutParams(
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        
        LinearLayout subView = (LinearLayout)findViewById(R.id.sensor_sub_view);
        AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
        alpha.setDuration(0);
        alpha.setFillAfter(true);
        subView.startAnimation(alpha);
        
        layout.setBackgroundResource(R.drawable.pic12);
        
        subView.addView(rightView);
        subView.addView(leftView);
        
        TextView textView1 = (TextView)findViewById(R.id.textView1);
		textView1.setText("壓力分佈");
        
        msg2User = (TextView) findViewById(R.id.msg_2_user);
        msg2User.setText("");
        
        state = 0;
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
