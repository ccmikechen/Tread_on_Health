package edu.kuas.mis.wmc.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;

import location.GPSLacationListener;
import location.GPSLacationManager;
import monitor.structure.Pattern;

import sensor.Direction;
import storage.UserInfoBox;
import triggers.BackPressTrigger;
import triggers.ForthBackSwingTrigger;
import triggers.ForthKickTrigger;
import triggers.ForthPressTrigger;
import triggers.JumpTrigger;
import triggers.LeftRightSwingTrigger;
import triggers.StepTrigger;
import triggers.TriggersManager;
import bluetooth.le.*;

import com.example.bluetooth.le.ChartActivity;
import com.example.bluetooth.le.DataGetterManager;
import com.example.bluetooth.le.R;

import data.DataRecorder;
import data.NikeDataGetter;
import data.NikeDataLogger;
import data.NikeDatabase;

import edu.kuas.mis.wmc.service.client.AssistantServiceClient;
import edu.kuas.mis.wmc.service.client.HoTDefine;
import edu.kuas.mis.wmc.service.client.HttpGetParamGenerater;
import edu.kuas.mis.wmc.service.client.ResponseErrorCode;
import edu.kuas.mis.wmc.service.client.ResponseErrorCodeDefine;
import edu.kuas.mis.wmc.service.client.ToHHttpGetParamGenerater;
import edu.kuas.mis.wmc.service.client.ToHRequestParams;
import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
	public static boolean debug = true;
	public static Context mContext;
	public static final int MESSAGE_SPORT_STATE_CHANGE = 0;
	public static final int MESSAGE_RX_ENABLE_SUCCESS = 1;
	public static final String SPORT_STATE = "SPORT_STATE";
	
	private static final long SCAN_PERIOD = 30000;
	private static final long KEEP_UPDATE_RUNNER = 3000;
	private static final long NOTIFY_CHECK_PERIOD = 10000;
	private static final long VIEW_INIT_PERIOD = 2500;
	// "E3:70:64:0D:64:45" 跟 37結尾、54結尾的SENSOR唯一對
		// "D6:9D:C7:8A:EA:7D" 跟 DF結尾、41結尾的SENSOR唯一對
		private static final String TARGET_DEVICE_ADDR = "E3:70:64:0D:64:45";	// Misfit addr
		private static final String HOST_ADDR =  "192.168.43.94";				// Server ip
	private static final String HOST_TARGET_FILE = "UserState";
	private static final int HOST_PORT = 8000;
	public static boolean isCharting = false;
	public static boolean runLine = false;
	
	private boolean mScanning = false;
	private boolean mConnected = false;
	private boolean mRxEnabled = false;
	private boolean mServiceBinded = false;
	private String mDeviceAddress;
	private int mUserStateNow;
	private int mReqSeq;
    private boolean paired = false;
    private int power = 0;
    private String targetUrl = null;
    
	
	private UserInfoBox mUserInfo;
	private Location mLocation;
	
	private TextView mStepView;
    private ImageView mMoreBtn;
    private ImageView mCameraBtn;
    private TextView mStateView;
    private ProgressBar powerBar;
    private LinearLayout powerBarLayout;
    private LinearLayout bkpicLayer;
	
	private BleService mUartService;
	private BluetoothAdapter mBluetoothAdapter;
	private GPSLacationManager mGPSLacationManager;
	private GPSListener mGPSListener;
	public static BluetoothLeService mBluetoothLeService;
	private NikeDataLogger logger;
	
	public static void startRegist() {
		mBluetoothLeService.registMonitor();
	}
	
	public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
	            case MESSAGE_SPORT_STATE_CHANGE:
	            	if(mBluetoothLeService.leftMonitor != null) {
		            	mUserStateNow = msg.getData().getInt("State");
		            	String state = getUserState(mUserStateNow);
		            	Util.log(state);
		            	stepsCalculate(state);
		            	mStateView.setText(state);
	            	}
	            	break;
	            case MESSAGE_RX_ENABLE_SUCCESS:
	            	mRxEnabled = true;
	            	mBluetoothLeService.registMonitor();
	            	break;
        	}
        }
	};
	
	private int m_steps = 100;
	
	private final Runnable stepsViewInit = new Runnable() {
	    public void run() {
	    	if(mConnected) {
	    		bkpicLayer = (LinearLayout) findViewById(R.id.bkpic_layer);
	    		bkpicLayer.setBackgroundResource(R.drawable.pic05);
		    	mStepView.setTextSize(80f);
		    	mStepView.setText("100");
		    	power = Integer.valueOf("100");
		    	powerBar.setProgress(power);
		    	//if(mUserInfo.getSteps().equals("")) mStepView.setText("0");
	    	}
	    }
	};
	
	private final Runnable scanPeriodEnd = new Runnable() {
	    public void run() {
	    	if(mScanning) stopScan();
	    }
	};
	
	private final Runnable notifyEnableCheck = new Runnable() {
	    public void run() {
	    	if(mConnected) {
		    	if(!mRxEnabled) {
		    		enableRxChar();
		    		mHandler.postDelayed(notifyEnableCheck, NOTIFY_CHECK_PERIOD);
		    	}
	    	}
	    }
	};
	
	private final Runnable keepingUpdateStateRunner = new Runnable() {
	    public void run() {
	    	if(mUserStateNow != -1) {Util.log("123");
	    		if(mBluetoothLeService.leftMonitor != null) {Util.log("456");
			    	int errorCode = updateDataToService(HoTDefine.OPCode.UPDATE_STATE, mUserStateNow).errorCode;
			    	if(errorCode > ResponseErrorCodeDefine.SUCCESS) Util.log("Error Code: " + errorCode);
			    	if(errorCode == ResponseErrorCodeDefine.SUCCESS) 
			    		mHandler.postDelayed(keepingUpdateStateRunner, KEEP_UPDATE_RUNNER);
	    		}
	    	} else {
	    		//startChartView();
	    	}
	    }
	};
	
	private final Runnable openLine = new Runnable() {
	    public void run() {
	    	//openLine();
	    	// reDir to Line user profile
	    	if(targetUrl != null) {
		    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
		    	startActivity(browserIntent);
	    	} else {
	    		Util.log("Target URL == null.");
	    	}
	    }
	};
	
	private final Runnable trainingViewStart = new Runnable() {
	    public void run() {
	    	mBluetoothLeService.unRegistMonitor();
	    	startChartView();
	    }
	}; 
	
	private ServiceConnection mServiceConnection = newServiceConnection();
	
	private ServiceConnection newServiceConnection() {
		return new ServiceConnection() {
	        @Override
	        public void onServiceConnected(ComponentName componentName, IBinder service) {
	            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
	            if(!mBluetoothLeService.initialize()) {
	                Util.log("Unable to initialize Bluetooth");
	                finish();
	            }
	            scanLeDevice(false);
	            mBluetoothLeService.connect(mDeviceAddress);
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName componentName) {
	            mBluetoothLeService = null;
	        }
	    };
	}
    
    private BluetoothAdapter.LeScanCallback mBLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	if(device.getAddress().equals(TARGET_DEVICE_ADDR)) {
                		mDeviceAddress = device.getAddress();
                		bleBackgroundServiceInit();
                	}
                }
            });
        }
    };
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            	whenConnectedDevice();
            	backgroundTimerInit();
            } else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            	whenDisconnectedDevice();
            } else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	whenDiscoveredSerivce();
            } else if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	whenCharReadCallBack();
            }
        }
    };
    
    private final BroadcastReceiver mBleStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            	if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
            		Toast.makeText(MainActivity.this, R.string.ble_state_off, Toast.LENGTH_SHORT).show();
            		MainActivity.this.finish();
                }
            }
        }
    };
    
    private void whenConnectedDevice() {
    	Util.log("Connected.");
        mConnected = true;
        mRxEnabled = false;
        mStepView.setTextSize(40f);
        mStepView.setText(R.string.connected_state);
        mBluetoothLeService.setMainHandler(mHandler);
        
        if(mScanning) scanLeDevice(false);
        mHandler.postDelayed(stepsViewInit, VIEW_INIT_PERIOD);
        mHandler.postDelayed(notifyEnableCheck, NOTIFY_CHECK_PERIOD);
    }
    
    private void whenDisconnectedDevice() {
    	Util.log("Disconnected.");
        mConnected = false;
        mRxEnabled = false;
        mStepView.setTextSize(40f);
        mStepView.setText(R.string.connecting_state);
        
        mBluetoothLeService.connect(mDeviceAddress);
        if(!mScanning) scanLeDevice(true);
    }
    
    private void whenDiscoveredSerivce() {
    	eachGattServices(mBluetoothLeService.getSupportedGattServices());
    	if(!mRxEnabled) {
    		enableRxChar();
    	}
    }
    
    private void whenCharReadCallBack() {
    	Util.log("Char read.");
    }
    
    private void enableRxChar() {
    	try {
	    	if(mUartService.chars.get(BleService.UART_RX) != null) {
		        boolean result = mBluetoothLeService.setCharacteristicNotification(mUartService.chars.get(BleService.UART_RX), true);
		        Util.log("Enable Rx " + result);
	    	} else {
	    		Util.log("*Enable Rx failed.");
	    	}
    	} catch(Exception e) {
    		Util.log("*enableRxChar() error: " + e.getMessage());
    	}
    }
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        
        return intentFilter;
    }
    
    private static IntentFilter makeBleStateIntentFilter() {
    	return new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    }
    
    private void eachGattServices(List<BluetoothGattService> gattServices) {
        if(gattServices == null) return;
        
        for(BluetoothGattService gattService:gattServices) {
        	if(gattService.getUuid().toString().equals(UartServiceUUID.UART_SERVICE)) {
        		mUartService = new BleService().setService(gattService);
        		eachGattCharacteristics(mUartService);
            }
        }
    }
    
    private void eachGattCharacteristics(BleService bleService) {
    	List<BluetoothGattCharacteristic> gattCharacteristics = bleService.service.getCharacteristics();
    	
        for(BluetoothGattCharacteristic gattCharacteristic:gattCharacteristics) {
        	if(gattCharacteristic.getUuid().toString().equals(UartServiceUUID.UART_TX)) {
        		bleService.chars.put(BleService.UART_TX, gattCharacteristic);
        	} else if(gattCharacteristic.getUuid().toString().equals(UartServiceUUID.UART_RX)) {
        		bleService.chars.put(BleService.UART_RX, gattCharacteristic);
        	}
        }
    }
    
    private void openLine() {
    	Intent intent = getPackageManager().getLaunchIntentForPackage("jp.naver.line.android");
    	startActivity(intent);
    }
    
    private ResponseErrorCode updateDataToService(int c, int a) {
		mReqSeq++;
		int s = mReqSeq;
		String i = "mingyulin0508";//mUserInfo.getLineID();
		String n = "周小倫";//mUserInfo.getName();
		double lv = /*mLocation.getLongitude();*/121.517712d;
		double lh = /*mLocation.getLatitude();*/25.049006d;
		ToHHttpGetParamGenerater getParams = new ToHHttpGetParamGenerater(new ToHRequestParams(s, c, i, n, lv, lh, a));
		return httpRequest(getParams);
	}
    
    private String getUserState(int state) {
		if(state == HoTDefine.State.STANDING)
			return HoTDefine.State.STANDING_STR;
		if(state == HoTDefine.State.WALKING)
			return HoTDefine.State.WALKING_STR;
		if(state == HoTDefine.State.RUNNING)
			return HoTDefine.State.RUNNING_STR;
		if(state == HoTDefine.State.SITTING)
			return HoTDefine.State.SITTING_STR;
		return HoTDefine.State.UNKNOW_STR; 
	}
    
    private void stepsCalculate(String state) {
		if(state.equals(HoTDefine.State.WALKING_STR) || state.equals(HoTDefine.State.RUNNING_STR)) {
			int steps = Integer.valueOf(mStepView.getText().toString());
			steps = steps + 1;
			if(!paired) {
				power = steps;
		    	powerBar.setProgress(power);
			}
			//mUserInfo.setSteps(String.valueOf(steps));
			//mStepView.setText(String.valueOf(steps));
		}
	}
    
    private void pairing() {
    	if(power >= 100) {
			if(!paired) {
				ResponseErrorCode response = updateDataToService(HoTDefine.OPCode.PAIR_OR_WAIT, mUserStateNow);
	    		int errorCode = response.errorCode;
	    		
	    		if(errorCode != ResponseErrorCodeDefine.SUCCESS) {
	    			Util.log("Pairing request fail. error code: " + errorCode);
	    			Toast.makeText(this, "還無法配對", Toast.LENGTH_SHORT).show();
	    		} else {
	    			String url = response.friendID;
	    			if(url != null) {
		    			targetUrl = url;
	    			} else {
	    				if(mUserInfo.getLineID().equals("kuasmis.wmc")) 
	    					targetUrl = "kuasmis.wmc";
	    				else if(mUserInfo.getLineID().equals("edisonlin58"))
	    					targetUrl = "edisonlin58";
	    				else // try catch
	    					targetUrl = "edisonlin58";
	    			}
	    			paired = true;
	    			power = 0;
	    			powerBar.setProgress(power);
	    			Toast.makeText(this, "配對中，請稍候...", 3500).show();
	    			mHandler.postDelayed(openLine, 3500l);
	    		}
			} else {
				Toast.makeText(this, "還無法配對", Toast.LENGTH_SHORT).show();
			}
    	} else {
			Toast.makeText(this, "能量還不夠哦", Toast.LENGTH_SHORT).show();
		}
    }
    
    private ResponseErrorCode httpRequest(HttpGetParamGenerater params) {
    	String paramsStr = "";
		try {
			paramsStr = URLEncoder.encode(params.getURLString(), "UTF-8");
		} catch (Exception e) {
			Util.log("httpRequest() error code: " + e.getMessage());
		}
    	return new AssistantServiceClient().doHttpGet(HOST_ADDR, HOST_PORT, HOST_TARGET_FILE, paramsStr);
    }
    
    private void scanLeDevice(final boolean enable) {
        if(enable) {
            startScan();
        } else {
        	stopScan();
        }
        invalidateOptionsMenu();
    }
    
    private void stopScan() {
    	Util.log("Scan Stop.");
    	mScanning = false;
        mBluetoothAdapter.stopLeScan(mBLeScanCallback);
    }
    
    private void startScan() {
    	if(!mConnected) {
	    	Util.log("Scan Start.");
	    	if(SCAN_PERIOD > 0) mHandler.postDelayed(scanPeriodEnd, SCAN_PERIOD);
	    	
	    	mScanning = true;
	        mBluetoothAdapter.startLeScan(mBLeScanCallback);
    	}
    }
    
    private void setDataGetters() {
        TriggersManager triggerManager = new TriggersManager();

        NikeDataGetter rightDataGetter = new NikeDataGetter(Direction.RIGHT);
        DataRecorder rightDataRecorder = new DataRecorder();
        mBluetoothLeService.addRightDataGetter(rightDataGetter);
        mBluetoothLeService.addRightDataGetter(rightDataRecorder);

        triggerManager.addTrigger(new StepTrigger(rightDataGetter, Direction.RIGHT));
        triggerManager.addTrigger(new ForthPressTrigger(rightDataGetter));
        triggerManager.addTrigger(new BackPressTrigger(rightDataGetter));
        triggerManager.addTrigger(new ForthKickTrigger(rightDataGetter));
        triggerManager.addTrigger(new ForthBackSwingTrigger(rightDataGetter));
        triggerManager.addTrigger(new LeftRightSwingTrigger(rightDataGetter));
        triggerManager.addTrigger(new JumpTrigger(rightDataGetter));

        DataGetterManager.setRightDataGetter(rightDataGetter);

        /******************************************************************************/

        NikeDataGetter leftDataGetter = new NikeDataGetter(Direction.LEFT);
        DataRecorder leftDataRecorder = new DataRecorder();
        mBluetoothLeService.addLeftDataGetter(leftDataGetter);
        mBluetoothLeService.addLeftDataGetter(leftDataRecorder);

        triggerManager.addTrigger(new StepTrigger(leftDataGetter, Direction.LEFT));
        triggerManager.addTrigger(new ForthPressTrigger(leftDataGetter));
        triggerManager.addTrigger(new BackPressTrigger(leftDataGetter));
        triggerManager.addTrigger(new ForthKickTrigger(leftDataGetter));
        triggerManager.addTrigger(new ForthBackSwingTrigger(leftDataGetter));
        triggerManager.addTrigger(new LeftRightSwingTrigger(leftDataGetter));
        triggerManager.addTrigger(new JumpTrigger(leftDataGetter));

        DataGetterManager.setLeftDataGetter(leftDataGetter);

        NikeDatabase database = null;
        try {
            database = new NikeDatabase(this, "NikeSensor.db");
            logger = new NikeDataLogger(database, leftDataRecorder, rightDataRecorder);
        } catch (SQLException e) {
            Util.log(e.getMessage());
        }
    }
    
    private void bleInit() {
    	if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    	
    	final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        if(!mBluetoothAdapter.isEnabled()) {
        	Toast.makeText(MainActivity.this, R.string.ble_state_off, Toast.LENGTH_SHORT).show();
        	mStepView.setText(R.string.ble_state_off);
        }
        
        scanLeDevice(true);
    }
    
    private void gpsInit() {
    	mGPSListener = new GPSListener();
    	mGPSLacationManager = new GPSLacationManager(mContext, mGPSListener);
    }
    
    private void startSettingView() {
    	final Intent intent = new Intent(this, SettingActivity.class);
    	startActivity(intent);
    }
    
    private void startCameraView() {
    	final Intent intent = new Intent(this, CameraActivity.class);
    	startActivity(intent);
    }
    
    private void startChartView() {
    	setDataGetters();
    	isCharting = true;
    	final Intent intent = new Intent(this, ChartActivity.class);
    	startActivity(intent);
    }
    
    private void bleBackgroundServiceInit() {
    	Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        if(!mServiceBinded) {
        	bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        	mServiceBinded = true;
        }
    }
    
    private void viewComInit() {
    	LinearLayout ml = (LinearLayout) findViewById(R.id.middle_layout);
    	ml.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		mHandler.postDelayed(trainingViewStart, 100);
			};
        });
    	LinearLayout ul = (LinearLayout) findViewById(R.id.up_layout);
    	ul.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		openLine();
			};
        });
    	bkpicLayer = (LinearLayout) findViewById(R.id.bkpic_layer);
    	bkpicLayer.setBackgroundResource(R.drawable.pic06);
    	powerBarLayout = (LinearLayout) findViewById(R.id.power_bar_layout);
    	powerBarLayout.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		if(Integer.parseInt(mStepView.getText().toString()) >= 100) {
        			pairing();
        		}
			};
        });
    	powerBar = (ProgressBar) findViewById(R.id.power_bar);
    	
    	mStepView = (TextView) findViewById(R.id.tv_Steps);
    	mStepView.setTextSize(40f);
    	mStepView.setText(R.string.connecting_state);
    	mStepView.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		mHandler.postDelayed(trainingViewStart, 100);
        		/*
        		if(mStepView.getText().toString().equals(R.string.ble_state_off)) {
        			MainActivity.this.finish();
        		}*/
			};
        });
    	mStateView = (TextView) findViewById(R.id.tv_State);
    	mMoreBtn = (ImageView) findViewById(R.id.btn_more);
    	mMoreBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		startSettingView();
			};
        });
    	mCameraBtn = (ImageView) findViewById(R.id.jump_btn);
    	mCameraBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		startCameraView();
			};
        });
    }
    
    private void preferencesStorageInit() {
    	mUserInfo = new UserInfoBox(mContext);
    }
    
    private void attributesInit() {
    	mUserStateNow = -1;
    	mReqSeq = -1;
    }
    
    private void backgroundTimerInit() {
    	int errorCode = updateDataToService(HoTDefine.OPCode.USER_CHECK, HoTDefine.State.STANDING).errorCode;
    	if(errorCode == ResponseErrorCodeDefine.SUCCESS) {
    		Util.log("Start Running.");
    		mUserStateNow = 0;
    		mHandler.postDelayed(keepingUpdateStateRunner, KEEP_UPDATE_RUNNER);
    	} else {
    		Util.log("Server Check Error: " + errorCode);
    		Toast.makeText(MainActivity.this, "伺服器沒有回應", Toast.LENGTH_LONG).show();
    	}
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        mContext = MainActivity.this;
        
        attributesInit();
        viewComInit();
        preferencesStorageInit();
        gpsInit();
        bleInit();
	}
	
	@Override
	 protected void onRestart() {
		super.onRestart();
		isCharting = false;
		startRegist();
		Util.log("onRestart");
	 }
	
	@Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        registerReceiver(mBleStateReceiver, makeBleStateIntentFilter());
        
        if(mBluetoothLeService != null) {
        	startScan();
        } else {
        	Util.log("BluetoothLeService error: null.");
        }
        isCharting = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        unregisterReceiver(mBleStateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceBinded) {
        	unbindService(mServiceConnection);
        	mServiceBinded = false;
        }
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    
    private class GPSListener implements GPSLacationListener {
		@Override
		public void onLocationChanged(Location location) {
			mLocation = location;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
    }
}
