package data;
import android.util.Log;

import sensor.Direction;
import sensor.NikeSensor;
import sensor.SensorPoints;
import sensor.ShoePoint;

public class NikeDataGetter implements DataGetterInterface {
	
	private Direction direction;
	private SensorPoints sensorPoints = null;
	private float pa, pb, pc, pd;
	private float x, y, z;
	private boolean keepShowData = false;
	
	public NikeDataGetter(Direction direction) {
		this(direction, false);
	}
	
	public NikeDataGetter(Direction direction, boolean showData) {	
		this.direction = direction;
		ShoePoint[] points = null;
		if (direction == Direction.RIGHT)
			points = NikeSensor.getRightSensorPoint();
		else if (direction == Direction.LEFT)
			points = NikeSensor.getLeftSensorPoint();
		
		this.sensorPoints = new SensorPoints(20, 50, points);
		this.keepShowData = showData;
	}
	
	@Override
	public void dataCallBack(float[] signals) {
		x = signals[0];
		y = signals[1];
		z = signals[2];
		pa = signals[3];
		pb = signals[4];
		pc = signals[5];
		pd = signals[6];
		if (keepShowData)
			showData(signals);
	}
	
	private void showData(float[] signals) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%.2f   ",
				Math.sqrt(signals[0] * signals[0] +
						signals[1] * signals[1] +
						signals[2] * signals[2])));
		for (int i = 0; i <= 2; i++)
			if (signals[i] < 0)
				sb.append(String.format("%.1f  ", signals[i]));
			else
				sb.append(String.format(" %.1f  ", signals[i]));
		for (int i = 3; i <= 6; i++)
			if (signals[i] < 0)
				sb.append(String.format("%8.0f", signals[i]));
			else
				sb.append(String.format(" %8.0f", signals[i]));
		sb.append("\n");
		Log.e("DATA", new String(sb));
	}
	
	synchronized public float getX() {
		return x;
	}
	
	synchronized public float getY() {
		return y;
	}
	
	synchronized public float getZ() {
		return z;
	}
	
	synchronized public float getA() {
		return pa;
	}
	
	synchronized public float getB() {
		return pb;
	}
	
	synchronized public float getC() {
		return pc;
	}
	
	synchronized public float getD() {
		return pd;
	}
	
	synchronized public float getG() {
		return (float) Math.sqrt(x*x + y*y + z*z); 
	}
	
	public ShoePoint getPointA() {
		return sensorPoints.getPointA();
	}
	
	public ShoePoint getPointB() {
		return sensorPoints.getPointB();
	}
	
	public ShoePoint getPointC() {
		return sensorPoints.getPointC();
	}
	
	public ShoePoint getPointD() {
		return sensorPoints.getPointD();
	}
	
	public int getWidth() {
		return sensorPoints.getWidth();
	}
	
	public int getHeight() {
		return sensorPoints.getHeight();
	}
	
	synchronized public ShoePoint getCenterOfPressurePoint() {
		return NikeSensor.getCenterOfPressurePoint(direction, pa, pb, pc, pd);
	}
	
}
