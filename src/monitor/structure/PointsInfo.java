package monitor.structure;
public class PointsInfo {
    public class Direction {
        public static final int DIRECTION_UP = 0;
        public static final int DIRECTION_KEEP = 1;
        public static final int DIRECTION_DOWN = 2;
        public static final int DIRECTION_UNKNOW = 3;
    }
    
    public float latestPoint;
    public float lastPoint;
    public float slope;
    public int direction;
    public float distance;
}
