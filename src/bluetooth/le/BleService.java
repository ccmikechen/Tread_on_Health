package bluetooth.le;

import java.util.HashMap;

import android.bluetooth.*;

public class BleService {
	public static final String UART_TX = "UART_TX";
	public static final String UART_RX = "UART_RX";
	
	public BluetoothGattService service;
	public HashMap<String, BluetoothGattCharacteristic> chars = new HashMap();
	
	public String SeriveName;

	public BleService setService(BluetoothGattService service) {
		this.service = service;
		return this;
	}
}
