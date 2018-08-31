/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bluetooth.le;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class BleGattAttributes {
    public static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String SIMPLES_KEY = "";
    public static String GYRO_SCOPE = "";
    
    static {
        //Services define.
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Battery");
        
        attributes.put("f000aa30-0451-4000-b000-000000000000", "Magnetometer");
        attributes.put("f000aa40-0451-4000-b000-000000000000", "Barometer");
        attributes.put("f000aa50-0451-4000-b000-000000000000", "Gyroscope");
        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "Simple Keys");
        attributes.put("f000aa20-0451-4000-b000-000000000000", "Humidity");
        attributes.put("f000aa10-0451-4000-b000-000000000000", "Accelerometer");
        attributes.put("f000aa00-0451-4000-b000-000000000000", "IR Temperature");
        attributes.put("82fe44c9-12f4-4bec-8d02-3c639c73a98e", "Custom Service");
        
        attributes.put("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "UART");
        attributes.put("4b550001-2d4d-4953-2d57-4d432d4c4142", "Hub Control");
        
        // Characteristics define.
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearence");
        attributes.put("01763871-da24-4d8b-b6ce-6458e544e94a", "Characteristic 1");
        attributes.put("1ba118ea-8f4d-44d5-ac55-69bed3853242", "Characteristic 2");
        
        attributes.put("6e400002-b5a3-f393-e0a9-e50e24dcca9e", "TX");
        attributes.put("6e400003-b5a3-f393-e0a9-e50e24dcca9e", "RX");
        
        attributes.put("4b550002-2d4d-4953-2d57-4d432d4c4142", "Hub Control IO");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null?defaultName:name;
    }
}
