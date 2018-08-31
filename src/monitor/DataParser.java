package monitor;

import monitor.structure.AccelerometerData;

public class DataParser {
    public static AccelerometerData parse(byte[] data) {
        float x, y, z;
        x = data[0];
        y = data[1];
        z = data[2];
        return new AccelerometerData(x, y, z);
    }
}
