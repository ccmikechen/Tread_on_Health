package com.example.bluetooth.le;

public class DataParser {
	public static float[] Parse(byte[] rawData) {
        final short REDUCE = 11;
        short x = 0, y = 0, z = 0;
        float resultX_1 = 0.0f, resultY_1 = 0.0f, resultZ_1 = 0.0f, resultX_2 = 0.0f, resultY_2 = 0.0f, resultZ_2 = 0.0f;
        int s1_1, s2_1, s3_1, s4_1, s1_2, s2_2, s3_2, s4_2;

        x = (short) ((rawData[11 - REDUCE] << 8) + rawData[12 - REDUCE] & 0xffff);
        y = (short) ((rawData[13 - REDUCE] << 8) + rawData[14 - REDUCE] & 0xffff);
        z = (short) ((rawData[15 - REDUCE] << 8) + rawData[16 - REDUCE] & 0xffff);
        resultX_1 = (x/1280f);
        resultY_1 = (y/1280f);
        resultZ_1 = (z/1280f);
        
        s1_1 = rawData[17 - REDUCE] & 0xff;
        s2_1 = rawData[18 - REDUCE] & 0xff;
        s3_1 = rawData[19 - REDUCE] & 0xff;
        s4_1 = rawData[20 - REDUCE] & 0xff;
        
        x = (short) (((rawData[21 - REDUCE] & 0xff) << 8) + rawData[22 - REDUCE] & 0xffff);
        y = (short) (((rawData[23 - REDUCE] & 0xff) << 8) + rawData[24 - REDUCE] & 0xffff);
        z = (short) (((rawData[25 - REDUCE] & 0xff) << 8) + rawData[26 - REDUCE] & 0xffff);
        resultX_2 = (x/1280f);
        resultY_2 = (y/1280f);
        resultZ_2 = (z/1280f);
        
        s1_2 = rawData[27 - REDUCE] & 0xff;
        s2_2 = rawData[28 - REDUCE] & 0xff;
        s3_2 = rawData[29 - REDUCE] & 0xff;
        s4_2 = rawData[30 - REDUCE] & 0xff;
        float[] resultDataArray = {resultX_1, resultY_1, resultZ_1, s1_1, s2_1, s3_1, s4_1, resultX_2, resultY_2, resultZ_2, s1_2, s2_2, s3_2, s4_2};
        return resultDataArray;
    }
}
