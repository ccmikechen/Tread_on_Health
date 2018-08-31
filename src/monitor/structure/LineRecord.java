package monitor.structure;

import monitor.DataCourseParser;
import monitor.PointsContainer;
import monitor.PointsRecognizer;

public class LineRecord {
    private int monitorNumber;
    private PointsContainer container = null;
    private float lastPointData;
    private boolean isAccel;
    
    public LineRecord(int monitorNumber, boolean isAccel) {
        this.lastPointData = 1024f;
        this.monitorNumber = monitorNumber;
        this.container = new PointsContainer(monitorNumber);
        this.isAccel = isAccel;
    }
    
    public PointsInfo[] add(float point) {
        if(lastPointData != 1024f) {
            container.add(new PointsRecognizer(point, lastPointData, isAccel));
        }
        lastPointData = point;
        return exportDataInfo();
    }
    
    public byte[] exportDataCourse() {
        if(container.getCount() == monitorNumber) {
            byte[] record = DataCourseParser.parse(container.getAllPoint());
            return record;
        }
        return null;
    }
    
    public PointsInfo[] exportDataInfo() {
        if(container.getCount() == monitorNumber) {
        	PointsRecognizer[] pointsRecognizers = container.getAllPoint();
        	int length = pointsRecognizers.length;
        	PointsInfo[] record = new PointsInfo[length];
            
            for(int i = 0;i < length;i++) {
            	record[i] = pointsRecognizers[i].calculate();
            }
            return record;
        }
        return null;
    }
    
    public void remove(int from) {
    	container.remove(from);
    }
    
    public void clear() {
    	container.removeAll();
    }
}
