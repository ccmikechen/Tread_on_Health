package bluetooth.le;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.bluetooth.le.DataParser;

import data.DataGetterInterface;

import monitor.NikeDataParser;
import monitor.PatternMonitor;
import monitor.UserStateControlInterface;
import monitor.structure.Pattern;

import edu.kuas.mis.wmc.app.MainActivity;
import edu.kuas.mis.wmc.app.Util;
import edu.kuas.mis.wmc.service.client.HoTDefine;

public class BluetoothLeService extends Service {
	public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public static final int SAMPLING_RATE = 36;
    
    private final IBinder mBinder;
    
    private Handler mHandler;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    public static PatternMonitor leftMonitor;
    public static PatternMonitor rightMonitor;
    private int mConnectionState = STATE_DISCONNECTED;
    private UserStateControl mUserStateControl;
    
    private class UserStateControl implements UserStateControlInterface {
		@Override
		public void onStateChange(int state) {
			Bundle bundle = new Bundle();
	        bundle.putInt("State", state);
			Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_SPORT_STATE_CHANGE);
	        msg.setData(bundle);
        	mHandler.sendMessage(msg);
		}
    }
    
    public BluetoothLeService() {
    	super();
    	mBinder = new LocalBinder();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                boolean result = mBluetoothGatt.discoverServices();
                //Util.log("Connected to GATT server.");
                //Util.log("Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(intentAction);
                //Util.log("Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Util.log("onServicesDiscovered received: " + status);
            }
        }
        
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            gatt.writeCharacteristic(characteristic);
        }
        
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if(mHandler != null) {/*
            	leftMonitor = new PatternMonitor(SAMPLING_RATE, new UserStateControl());
                //rightMonitor = new PatternMonitor(SAMPLING_RATE, new UserStateControl());
                leftMonitor.setTag("LEFT");
                //rightMonitor.setTag("RIGHT");
                */
            	Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_RX_ENABLE_SUCCESS);
            	mHandler.sendMessage(msg);
            } else
            	Util.log("Handler is null.");
        }
        
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        	if(UartServiceUUID.UART_RX.equals(characteristic.getUuid().toString())) {
            	final byte[] data = characteristic.getValue();
            	if(!MainActivity.isCharting) dataHandle(data);
            	if(MainActivity.isCharting) dataHandle2(data);
            }
        }
    };
    
    public void registMonitor() {
    	leftMonitor = new PatternMonitor(SAMPLING_RATE, new UserStateControl());
        rightMonitor = new PatternMonitor(SAMPLING_RATE, new UserStateControl());
        leftMonitor.setTag("LEFT");
        rightMonitor.setTag("RIGHT");
    }
    
    public void unRegistMonitor() {
    	leftMonitor = null;
    	rightMonitor = null;
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        
        if(!UartServiceUUID.UART_RX.equals(characteristic.getUuid().toString())) {
            final byte[] data = characteristic.getValue();
            if(data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + " (" + data.length +")" + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }
    
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    
    private byte[] dataHandle(byte[] data) {
    	if(leftMonitor != null)
    		leftMonitor.feed(NikeDataParser.parse(data));
    	if(rightMonitor != null) {
    		byte[] b = new byte[]{data[10], data[11], data[12], data[13], data[14], data[15], data[16], data[17], data[18], data[19]};
    		rightMonitor.feed(NikeDataParser.parse(b));
    	}
    	return null;
    }
    
    private void dataHandle2(byte[] sensorData) {
    	if(sensorData != null && sensorData.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(sensorData.length);
            float[] data = DataParser.Parse(sensorData);

            dataCallBack(leftDataGetterList, new float[] {
                data[0], data[1], data[2], data[3], data[4], data[5], data[6]
            });

            dataCallBack(rightDataGetterList, new float[] {
                data[7], data[8], data[9], data[10], data[11], data[12], data[13]
            });
		}
    }
    
    private void dataCallBack(List<DataGetterInterface> dataGetters, float[] data) {
        for (DataGetterInterface dataGetter : dataGetters)
            dataGetter.dataCallBack(data);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if(mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBluetoothManager == null) {
                Util.log("Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null) {
            Util.log("Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public boolean connect(final String address) {
        if(mBluetoothAdapter == null || address == null) {
            Util.log("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if(mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Util.log("Trying to use an existing mBluetoothGatt for connection.");
            if(mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if(device == null) {
            Util.log("Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if(mBluetoothAdapter == null || mBluetoothGatt == null) {
            Util.log("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if(mBluetoothGatt == null) return;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(mBluetoothAdapter == null || mBluetoothGatt == null) {
            Util.log("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(mBluetoothAdapter == null || mBluetoothGatt == null) {
            Util.log("BluetoothAdapter not initialized");
            return false;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
        return true;
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if(mBluetoothAdapter == null || mBluetoothGatt == null) {
            Util.log("BluetoothAdapter not initialized");
            return false;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return mBluetoothGatt.writeDescriptor(descriptor);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if(mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    
    public void setMainHandler(Handler handler) {
    	mHandler = handler;
    }
    
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
    
    public static void bindCamera(Handler mHandler) {
    	leftMonitor.bindCamera(mHandler);
    }
    
    public static void unBindCamera() {
    	leftMonitor.unBindCamera();
    }
    
    
    private List<DataGetterInterface> rightDataGetterList =
            new ArrayList<DataGetterInterface>();
    private List<DataGetterInterface> leftDataGetterList =
            new ArrayList<DataGetterInterface>();
    
    public void addLeftDataGetter(DataGetterInterface dataGetter) {
        leftDataGetterList.add(dataGetter);
    }

    public void addRightDataGetter(DataGetterInterface dataGetter) {
        rightDataGetterList.add(dataGetter);
    }
}
