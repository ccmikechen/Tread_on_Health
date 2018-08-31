package monitor;

import monitor.structure.CourseType;
import monitor.structure.PointsInfo;

public class DataCourseParser {
    public static byte[] parse(PointsRecognizer[] pointsRecognizers) {
        int length = pointsRecognizers.length;
        byte[] courses = new byte[length];
        
        for(int i = 0;i < length;i++) {
            PointsInfo info = pointsRecognizers[i].calculate();
            
            switch(info.direction) {
                case PointsInfo.Direction.DIRECTION_UP:
                    courses[i] = (byte)CourseType.UP;
                    break;
                case PointsInfo.Direction.DIRECTION_KEEP:
                    courses[i] = (byte)CourseType.KEEP;
                    break;
                case PointsInfo.Direction.DIRECTION_DOWN:
                    courses[i] = (byte)CourseType.DOWN;
                    break;
                case PointsInfo.Direction.DIRECTION_UNKNOW:
                    break;
            }
        }
        return courses;
    }
    
    public static byte[] parse(PointsInfo[] pointsInfos) {
        int length = pointsInfos.length;
        byte[] courses = new byte[length];
        
        for(int i = 0;i < length;i++) {
            PointsInfo info = pointsInfos[i];
            
            switch(info.direction) {
                case PointsInfo.Direction.DIRECTION_UP:
                    courses[i] = (byte)CourseType.UP;
                    break;
                case PointsInfo.Direction.DIRECTION_KEEP:
                    courses[i] = (byte)CourseType.KEEP;
                    break;
                case PointsInfo.Direction.DIRECTION_DOWN:
                    courses[i] = (byte)CourseType.DOWN;
                    break;
                case PointsInfo.Direction.DIRECTION_UNKNOW:
                    break;
            }
        }
        return courses;
    }
}
