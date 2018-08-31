package monitor;

import monitor.structure.AccelerometerData;
import monitor.structure.NikeSensorData;

public class NikeDataParser extends DataParser {
    public static AccelerometerData parse(byte[] data) {
        float x, y, z;
        int s1, s2, s3, s4;
        
        x = ((short)((data[0] << 8) + data[1])/4096.0f);
		y = ((short)((data[2] << 8) + data[3])/4096.0f);
		z = ((short)((data[4] << 8) + data[5])/4096.0f);
		s1 = data[6] & 0xFF;
		s2 = data[7] & 0xFF;
		s3 = data[8] & 0xFF;
		s4 = data[9] & 0xFF;
		
        return new NikeSensorData(x, y, z, s1, s2, s3, s4);
    }
}
