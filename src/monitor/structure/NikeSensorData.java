package monitor.structure;
public class NikeSensorData extends AccelerometerData {
    public class DescriptionLineType {
        public static final byte X = (byte)0x00;
        public static final byte Y = (byte)0x01;
        public static final byte Z = (byte)0x02;
        public static final byte S1 = (byte)0x03;
        public static final byte S2 = (byte)0x04;
        public static final byte S3 = (byte)0x05;
        public static final byte S4 = (byte)0x06;
        public static final byte X_VALUE = (byte)0x07;
        public static final byte Y_VALUE = (byte)0x08;
        public static final byte Z_VALUE = (byte)0x09;
        public static final byte S1_VALUE = (byte)0x0A;
        public static final byte S2_VALUE = (byte)0x0B;
        public static final byte S3_VALUE = (byte)0x0C;
        public static final byte S4_VALUE = (byte)0x0D;
    }
    
    public int s1, s2, s3, s4;

    public NikeSensorData(float x, float y, float z) {
        super(x, y, z);
    }
    
    public NikeSensorData(float x, float y, float z, int s1, int s2, int s3, int s4) {
        super(x, y, z);
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
    }
    
    public void setPressureData(int s1, int s2, int s3, int s4) {
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
    }
}
