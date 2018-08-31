package edu.kuas.mis.wmc.app;

import java.io.FileOutputStream;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.widget.ImageView;

public class CameraManager {
	private final int AUTO_FOCUS = 0;
	private final int TAKE_PIC = 1;
	private final int START_PREVIEW = 2;
	private final int STOP_CAM = 3;
	private final int START_CAM = 4;
	
	private Camera camera = null;
	
	private Context mContext;
	private Handler mHandler;
	
	private boolean isCamOpen = false;
	
	public CameraManager(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
	}
	
	private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {
	    public void onAutoFocus(boolean success, Camera camera) {
			if(success) {
				Util.log("對焦成功");
				enableTakeButton();
			}
	    }
    };
	
    private PictureCallback jpeg = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Util.log("拍照成功");
			final Bitmap bmp = retateImage(BitmapFactory.decodeByteArray(data, 0, data.length));
			
			dialopSetUp(bmp);
		}
	};
	
	public boolean startCamera(SurfaceHolder surfaceHolder) {
		if(!camErrorCheck(START_CAM)) return false;
		if(!camParamInit(surfaceHolder)) return false;
		return true;
	}
	
	public boolean stopCamera() {
		return camErrorCheck(STOP_CAM);
	}
	
	public boolean autoFocus() {
		return camErrorCheck(AUTO_FOCUS);
	}
	
	public boolean takePicture() {
		return camErrorCheck(TAKE_PIC);
	}
	
	private boolean startPreview() {
		return camErrorCheck(START_PREVIEW);
	}
	
	private boolean camErrorCheck(int cmd) {
		if(isCamOpen) {
			try {
				switch(cmd) {
		            case AUTO_FOCUS:
		            	disableTakeButton();
		            	camera.autoFocus(mAutoFocusCallback);
						break;
		            case TAKE_PIC:
		            	camera.takePicture(null, null, jpeg);
						break;
		            case START_PREVIEW:
						camera.startPreview();
						break;
		            case STOP_CAM:
		            	mAutoFocusCallback = null;
						jpeg = null;
		            	camera.stopPreview();
		    			camera.release();
		    			isCamOpen = false;
		    			break;
					default:
						return false;
				}
			} catch(Exception e) {
				Util.log("Camera check: " + e.getMessage());
				return false;
			}
			return true;
		} else {
			try {
				switch(cmd) {
					case START_CAM:
		            	camera = Camera.open();
		    			isCamOpen = true;
		    			break;
					default:
						return false;
				}
			} catch(Exception e) {
				Util.log("Camera check: " + e.getMessage());
				return false;
			}
			return true;
		}
	}
	
	private Bitmap retateImage(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
	
	private boolean camParamInit(SurfaceHolder surfaceHolder) {
		try {
			camera.setDisplayOrientation(90);
			
			Camera.Parameters param = null;
			Camera.Size bestSize = null;
			List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
			bestSize = sizeList.get(0);
			
			param = camera.getParameters();
			param.setPictureFormat(PixelFormat.JPEG);
			param.setPreviewFrameRate(30);
			param.setPreviewSize(420, 320);
			//param.setPictureSize(3264, 2448);
			param.setPictureSize(bestSize.width, bestSize.height);
			camera.setParameters(param);
			
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
			return true;
		} catch(Exception e) {
			Util.log("Camera error: " + e.getMessage());
			return false;
		}
	}
    
    private void dialopSetUp(final Bitmap bmp) {
    	try {
			ImageView iv = new ImageView(mContext);
			iv.setImageBitmap(bmp);
			
			final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(mContext);
			dialog.setTitle("Choose the Photo?")
			.setView(iv).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					photoSave(bmp, "/sdcard/testPics/dd.png");
					stopCamera();
					finish();
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					startPreview();
					autoFocus();
				}
			}).show(); 
		} catch(Exception e) {
			Util.log(e.getMessage());
		}
    }
	
	private boolean photoSave(final Bitmap bmp, String fullPath) {
    	try {
			FileOutputStream fileOutputStream = new FileOutputStream(fullPath);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
			fileOutputStream.close();
			return true;
		} catch (Exception e) {
			Util.log(e.getMessage());
			return false;
		}
    }
	

	private void enableTakeButton() {
		Message msg = mHandler.obtainMessage(CameraActivity.MESSAGE_ENABLE_TAKE_BUTTON);
        mHandler.sendMessage(msg);
	}
	
	private void disableTakeButton() {
		Message msg = mHandler.obtainMessage(CameraActivity.MESSAGE_DISABLE_TAKE_BUTTON);
        mHandler.sendMessage(msg);
	}
	
	private void finish() {
		Message msg = mHandler.obtainMessage(CameraActivity.MESSAGE_FINISH);
        mHandler.sendMessage(msg);
	}
}
