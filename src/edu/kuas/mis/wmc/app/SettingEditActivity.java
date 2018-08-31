package edu.kuas.mis.wmc.app;

import com.example.bluetooth.le.R;

import storage.UserInfoBox;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingEditActivity extends Activity {
	private Button save = null;
	private EditText text = null;
	private TextView titleBar = null;
	
	private int position = -1;
	
	private UserInfoBox mUserInfoBox = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_edit_activity);
		
		mUserInfoBox = new UserInfoBox(this);
		
		save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		switch(position) {
    			case 0:
    				mUserInfoBox.setName(text.getText().toString());
    				break;
    			case 1:
    				mUserInfoBox.setLineID(text.getText().toString());
    				break;
    			case 2:
    				mUserInfoBox.setGender(text.getText().toString());
    				break;
    			case 3:
    				mUserInfoBox.setHeight(text.getText().toString());
    				break;
    			case 4:
    				mUserInfoBox.setWeight(text.getText().toString());
    				break;
        		}
        		SettingEditActivity.this.finish();
			};
        });
		text = (EditText)findViewById(R.id.edit_title);
		titleBar = (TextView)findViewById(R.id.title_bar);
		titleBar.setHeight(120);
		titleBar.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        
		Bundle bundle = this.getIntent().getExtras();
        this.position = bundle.getInt("title");
        String titleStr = null;
        String contentStr = null;
        
		switch(position) {
			case 0:
				titleStr = "姓名";
				contentStr = mUserInfoBox.getName();
				break;
			case 1:
				titleStr = "Line ID";
				contentStr = mUserInfoBox.getLineID();
				break;
			case 2:
				titleStr = "性別";
				contentStr = mUserInfoBox.getGender();
				break;
			case 3:
				titleStr = "身高";
				contentStr = mUserInfoBox.getHeight();
				break;
			case 4:
				titleStr = "體重";
				contentStr = mUserInfoBox.getWeight();
				break;
		}
		titleBar.setText(titleStr);
		text.setText(contentStr);
	}
}
