package monitor.structure;

import java.util.HashMap;


public class PatternDescription extends PatternCourseDescription {
	public HashMap<Byte, float[]> valueLimit = null;
	
	public PatternDescription(int posture, byte[] x, float[] xValue,
										   byte[] y, float[] yValue,
										   byte[] z, float[] zValue,
										   byte[] s1, float[] s1Value,
										   byte[] s2, float[] s2Value,
										   byte[] s3, float[] s3Value,
										   byte[] s4, float[] s4Value) {
		super(posture, x, y, z, s1, s2, s3, s4);
		valueLimit = new HashMap();
		
		if(xValue != null)
			this.valueLimit.put(NikeSensorData.DescriptionLineType.X_VALUE, xValue);
		if(yValue != null)
			this.valueLimit.put(NikeSensorData.DescriptionLineType.Y_VALUE, yValue);
		if(zValue != null)
			this.valueLimit.put(NikeSensorData.DescriptionLineType.Z_VALUE, zValue);
		if(s1Value != null)
			this.valueLimit.put(NikeSensorData.DescriptionLineType.S1_VALUE, s1Value);
		if(s2Value != null)
			this.valueLimit.put(NikeSensorData.DescriptionLineType.S2_VALUE, s2Value);
		if(s3Value != null)
			this.valueLimit.put(NikeSensorData.DescriptionLineType.S3_VALUE, s3Value);
		if(s4Value != null)
			this.valueLimit.put(NikeSensorData.DescriptionLineType.S4_VALUE, s4Value);
	}
}
