package edu.kuas.mis.wmc.app;

import java.util.ArrayList;
import java.util.HashMap;

import storage.UserInfoBox;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class SettingActivity extends ListActivity {
	private final String TITLE = "TITLE";
	private final String SUBTITLE = "SUBTITLE";
	
	private Context mContext;
	private UserInfoBox mUserInfo;
	
	private void viewComInit() {
		TextView header = new TextView(this);
		header.setText("個人資料");
		header.setTextSize(30);
		header.setTextColor(Color.rgb(255, 255, 255));
		header.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		header.setHeight(120);
		header.setBackgroundColor(Color.rgb(35, 153, 192));
		ListView listView = (ListView)this.getListView();
        ArrayList<HashMap<String,String>> myListData = new ArrayList<HashMap<String, String>>();
        addListData(myListData);
        listView.addHeaderView(header);
        SimpleAdapter adapter = new SimpleAdapter(this, myListData, android.R.layout.simple_list_item_2, 
        		new String[] {TITLE, SUBTITLE}, new int[] {android.R.id.text1, android.R.id.text2}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.rgb(0, 0, 0));
                return view;
            }
        };;
        this.setListAdapter(adapter);
        OnItemClickListener onclickListener = new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				editSubtitle(position);
			}
        };
        listView.setOnItemClickListener(onclickListener);
        listView.setCacheColorHint(Color.rgb(36, 120, 153));
        listView.setBackgroundColor(Color.rgb(255, 255, 255));
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        mContext = SettingActivity.this;
        mUserInfo = new UserInfoBox(mContext);
        viewComInit();
	}
	
	@Override
    public void onDestroy() {
		super.onDestroy();
	}
	
	private void editSubtitle(int position) {
		final Intent intent = new Intent(this, SettingEditActivity.class);
        intent.putExtra("title", position-1); 
    	startActivity(intent);
	}
	
	private void addListData(ArrayList<HashMap<String,String>> myListData) {
		mUserInfo.setName("周小倫");
		mUserInfo.setLineID("mingyulin0508");
		mUserInfo.setGender("男");
		mUserInfo.setHeight("180");
		mUserInfo.setWeight("65");
		//mUserInfo.setPower(power)
		mUserInfo.setSteps("100");
		
		HashMap<String,String> item = new HashMap<String,String>();
        item.put(TITLE, "姓名");
        item.put(SUBTITLE, /*mUserInfo.getName()*/"周小倫");
        myListData.add(item);
        
        item = new HashMap<String,String>();
        item.put(TITLE, "Line ID");
        item.put(SUBTITLE, /*mUserInfo.getLineID()*/"mingyulin0508");
        myListData.add(item);
        
        item = new HashMap<String,String>();
        item.put(TITLE, "性別");
        item.put(SUBTITLE, /*mUserInfo.getGender()*/"男");
        myListData.add(item);
        
        item = new HashMap<String,String>();
        item.put(TITLE, "身高");
        if(mUserInfo.getHeight().equals("") || mUserInfo.getHeight() == null)
        	item.put(SUBTITLE, "");
        else
        	item.put(SUBTITLE, /*mUserInfo.getHeight() +*/ "180公分");
        myListData.add(item);
        
        item = new HashMap<String,String>();
        item.put(TITLE, "體重");
        if(mUserInfo.getWeight().equals("") || mUserInfo.getWeight() == null)
        	item.put(SUBTITLE, "");
        else
        	item.put(SUBTITLE, /*mUserInfo.getWeight() + */"65公斤");
        myListData.add(item);
	}
}
