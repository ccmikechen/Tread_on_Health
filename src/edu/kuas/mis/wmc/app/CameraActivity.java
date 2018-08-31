package edu.kuas.mis.wmc.app;

import com.example.bluetooth.le.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
	public static final int MESSAGE_ENABLE_TAKE_BUTTON = 0;
	public static final int MESSAGE_DISABLE_TAKE_BUTTON = 1;
	public static final int MESSAGE_TAKE_PIC = 2;
	public static final int MESSAGE_FINISH = 3;
	
	private SurfaceView surfaceView;
	private Button take;
	
	private Context mContext = this;
	private CameraManager cameraManager;
	private SurfaceHolder surfaceHolder;

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
	            case MESSAGE_ENABLE_TAKE_BUTTON:
	            	take.setEnabled(true);
	            	break;
	            case MESSAGE_DISABLE_TAKE_BUTTON:
	            	take.setEnabled(false);
	            	break;
	            case MESSAGE_TAKE_PIC:
	            	cameraManager.takePicture();
	            case MESSAGE_FINISH:
	            	CameraActivity.this.finish();
	            	break;
        	}
        }
	};
	
	private final Runnable cameraInit = new Runnable() {
	    public void run() {
	    	cameraManager.startCamera(surfaceHolder);
	    	cameraManager.autoFocus();
	    }
	};

	private void viewComInit() {
		setRequestedOrientation(1);
	
		take = (Button)findViewById(R.id.take);
		take.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				cameraManager.takePicture();
			}
		});
		
		surfaceView = (SurfaceView)findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public void showDialog(CharSequence msg) {
    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
	
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		//當預覽界面的格式和大小改變時調用此方法
	}

	public void surfaceCreated(SurfaceHolder holder) {
		//當預覽界面創建時調用此方法
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		//當預覽界面被關閉時調用此方法
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_activity);
		
		MainActivity.mBluetoothLeService.bindCamera(mHandler);
		cameraManager = new CameraManager(mContext, mHandler);
		viewComInit();
		
		mHandler.postDelayed(cameraInit, 800);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mBluetoothLeService.unBindCamera();
    }
}
