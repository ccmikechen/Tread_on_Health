package triggers;

import android.util.Log;

import sensor.Direction;
import sensor.ShoePoint;
import data.NikeDataGetter;


public class StepTrigger extends Trigger {

	private NikeDataGetter getter = null;
	private Direction direction;
	private boolean isBackPressed = false;
	private boolean isForthPressed = false;
	
	private float a, b, c, d;
	
	private double totalAngle = 0f;
	private int logCount = 0;
	private long startTime;
	private ShoePoint currentPoint;
	
	
	
	public StepTrigger(NikeDataGetter getter, Direction direction) {
		this.getter = getter;
		this.direction = direction;
	}
	
	@Override
	protected boolean triggered() {
		a = getter.getA();
		b = getter.getB();
		c = getter.getC();
		d = getter.getD();
		
		return d > 30 ||
				(isBackPressed && 
						(a > 30 || !isForthPressed && (b > 30 || c > 30)));
	}

	@Override
	protected void doTriggeredAction() {
		ShoePoint cop = getter.getCenterOfPressurePoint();
		
		if (isBackPressed == false) {
			startTime = System.nanoTime();
			currentPoint = cop;
			totalAngle = getCOPAngle(cop);
			logCount = 1;
		}
		
		if (currentPoint.y <= getter.getPointB().y &&
				!currentPoint.equals(cop)) {
			double angle = getCOPAngle(cop);
			if (angle < 110 && angle >= 90 && cop.y >= currentPoint.y) {
				totalAngle += getCOPAngle(cop);
				logCount++;
				currentPoint = cop;
			}
		}
		
		isBackPressed = true;
		if (a > 30 && d < 30) isForthPressed = true;
		
	}
	
	private double getCOPAngle(ShoePoint cop) {
		double angle = Math.atan2(cop.y - getter.getPointD().y,
				                  cop.x - getter.getPointD().x);
		return Math.max(0, angle / Math.PI * 180);
	}

	@Override
	protected void doNoTriggeredAction() {
		if (isBackPressed == true && isForthPressed == true)
			logData();
		
		isBackPressed = false;
		isForthPressed = false;
	}

	private void logData() {
		double averageAngle = totalAngle / logCount;
		double stanceTime = (System.nanoTime() - startTime) / 1e9;
		
		Log.e(TAG, direction == Direction.RIGHT ? "Right" : "Left");
		Log.e(TAG, "Time: " + stanceTime + " sec");
		Log.e(TAG, "Average Angle: " + averageAngle);
		Log.e(TAG, getAngleResult(averageAngle));
	}
	
	private String getAngleResult(double angle) {
		if (direction == Direction.RIGHT) {
			if (angle > 100)
				return "內旋";
			else if (angle < 95)
				return "外旋";
		} else if (direction == Direction.LEFT) {
			if (angle > 85)
				return "外旋";
			else if (angle < 80)
				return "內旋";
		}
		return "正常";
	}
	
}
