package monitor.structure;

import java.util.HashMap;

public class PatternCourseDescription {
	public int posture;
	public HashMap<Byte, byte[]> patterns = null;
	
	public PatternCourseDescription(int posture, byte[] x, byte[] y, byte[] z, byte[] s1, byte[] s2, byte[] s3, byte[] s4) {
		this.patterns = new HashMap();
		this.posture = posture;
		
		if(x != null)
			this.patterns.put(NikeSensorData.DescriptionLineType.X, x);
		if(y != null)
			this.patterns.put(NikeSensorData.DescriptionLineType.Y, y);
		if(z != null)
			this.patterns.put(NikeSensorData.DescriptionLineType.Z, z);
		if(s1 != null)
			this.patterns.put(NikeSensorData.DescriptionLineType.S1, s1);
		if(s2 != null)
			this.patterns.put(NikeSensorData.DescriptionLineType.S2, s2);
		if(s3 != null)
			this.patterns.put(NikeSensorData.DescriptionLineType.S3, s3);
		if(s4 != null)
			this.patterns.put(NikeSensorData.DescriptionLineType.S4, s4);
	}
}
