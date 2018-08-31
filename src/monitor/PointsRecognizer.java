package monitor;

import monitor.structure.PointsInfo;

public class PointsRecognizer {
    private final float AXIS_START = 0;
    private final float AXIS_GAP = 1;
    
    private float latestPoint;
    private float lastPoint;
    private boolean isAccel;
    
    public PointsRecognizer() {
    }
    
    public PointsRecognizer(float latestPoint, float lastPoint, boolean isAccel) {
        this();
        setPoint(latestPoint, lastPoint);
        monitorAccel(isAccel);
    }
    
    public void setPoint(float latestPoint, float lastPoint) {
        this.latestPoint = latestPoint;
        this.lastPoint = lastPoint;
    }
    
    public void monitorAccel(boolean isAccel) {
    	this.isAccel = isAccel;
    }
    
    public PointsInfo calculate() {
        PointsInfo lineInfo = new PointsInfo();
        lineInfo.latestPoint = latestPoint;
        lineInfo.lastPoint = lastPoint;
        lineInfo.slope = calculateSlope();
        lineInfo.direction = calculateDirection();
        lineInfo.distance = calculateDistance();
        return lineInfo;
    }
    
    private float calculateSlope() {
        return (float)(latestPoint - lastPoint) / (float)(AXIS_START - AXIS_GAP);
    }
    
    private int calculateDirection() {
        float difference = (float)(latestPoint - lastPoint);
        if(difference < 0f) {
        	if(isAccel) {
        		if(Math.abs(difference) > 0.07f) {
        			return PointsInfo.Direction.DIRECTION_DOWN;
        		} else {
        			return PointsInfo.Direction.DIRECTION_KEEP;
        		}
        	} else
        		return PointsInfo.Direction.DIRECTION_DOWN;
        } else if(difference > 0f) {
        	if(isAccel) {
        		if(Math.abs(difference) > 0.07f) {
        			return PointsInfo.Direction.DIRECTION_UP;
        		} else {
        			return PointsInfo.Direction.DIRECTION_KEEP;
        		}
        	} else
        		return PointsInfo.Direction.DIRECTION_UP;
        } else if(difference == 0f) {
            return PointsInfo.Direction.DIRECTION_KEEP;
        } else {
            return PointsInfo.Direction.DIRECTION_UNKNOW;
        }
    }
    
    private float calculateDistance() {
        return (float)Math.sqrt((float)Math.pow((AXIS_START - AXIS_GAP), 2) + (float)Math.pow((latestPoint - lastPoint), 2));
    }
}
