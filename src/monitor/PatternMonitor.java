package monitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import bluetooth.le.BluetoothLeService;
import monitor.structure.AccelerometerData;
import monitor.structure.LineRecord;
import monitor.structure.LogicType;
import monitor.structure.NikeSensorData;
import monitor.structure.Pattern;
import monitor.structure.PatternDescription;
import monitor.structure.PointsInfo;
import edu.kuas.mis.wmc.app.CameraActivity;
import edu.kuas.mis.wmc.app.MainActivity;
import edu.kuas.mis.wmc.app.Util;
import edu.kuas.mis.wmc.service.client.HoTDefine;

public class PatternMonitor {
    private int monitorNumber;
    private String tag = null;
    private LineRecord xAxisRecord = null;
    private LineRecord yAxisRecord = null;
    private LineRecord zAxisRecord = null;
    private LineRecord s1Record = null;
    private LineRecord s2Record = null;
    private LineRecord s3Record = null;
    private LineRecord s4Record = null;
    private UserStateControlInterface mUserStateControl = null;
    private Handler camHandler = null;
    
    public PatternMonitor(int monitorNumber, UserStateControlInterface userStateControl) {
        this.monitorNumber = monitorNumber;
        this.xAxisRecord = new LineRecord(monitorNumber, true);
        this.yAxisRecord = new LineRecord(monitorNumber, true);
        this.zAxisRecord = new LineRecord(monitorNumber, true);
        this.s1Record = new LineRecord(monitorNumber, false);
        this.s2Record = new LineRecord(monitorNumber, false);
        this.s3Record = new LineRecord(monitorNumber, false);
        this.s4Record = new LineRecord(monitorNumber, false);
        this.mUserStateControl = userStateControl;
    }
    
    public void bindCamera(Handler handler) {
    	this.camHandler = handler;
    }
    
    public void unBindCamera() {
    	this.camHandler = null;
    }
    
    public void setTag(String tag) {
    	this.tag = tag;
    }
    
    public String getTag() {
    	return tag;
    }
    
    public void feed(AccelerometerData data) {
        NikeSensorData nikeData = (NikeSensorData)data;
        PointsInfo[] x = xAxisRecord.add(nikeData.x);
        PointsInfo[] y = yAxisRecord.add(nikeData.y);
        PointsInfo[] z = zAxisRecord.add(nikeData.z);
        PointsInfo[] s1 = s1Record.add(nikeData.s1);
        PointsInfo[] s2 = s2Record.add(nikeData.s2);
        PointsInfo[] s3 = s3Record.add(nikeData.s3);
        PointsInfo[] s4 = s4Record.add(nikeData.s4);
        
        byte[] xDir = DataCourseParser.parse(x);
        byte[] yDir = DataCourseParser.parse(y);
        byte[] zDir = DataCourseParser.parse(z);
        byte[] s1Dir = DataCourseParser.parse(s1);
        byte[] s2Dir = DataCourseParser.parse(s2);
        byte[] s3Dir = DataCourseParser.parse(s3);
        byte[] s4Dir = DataCourseParser.parse(s4);
        
        float[] xValue = getPointsRuleValue(x);
        float[] yValue = getPointsRuleValue(y);
        float[] zValue = getPointsRuleValue(z);
        float[] s1Value = getPointsRuleValue(s1);
        float[] s2Value = getPointsRuleValue(s2);
        float[] s3Value = getPointsRuleValue(s3);
        float[] s4Value = getPointsRuleValue(s4);
        
        compare(xDir, yDir, zDir, s1Dir, s2Dir, s3Dir, s4Dir, xValue, yValue, zValue, s1Value, s2Value, s3Value, s4Value);
    }

    // Version 2: Compare line points Goes direction and any direction's value if accord the pattern value.
    private void compare(byte[] x, byte[] y, byte[] z, byte[] s1, byte[] s2, byte[] s3, byte[] s4, 
    		float[] xValue, float[] yValue, float[] zValue, float[] s1Value, float[] s2Value, float[] s3Value, float[] s4Value) {
    	boolean isBelongAnyPattern = false;
    	
    	if(camHandler != null) {
    		if(patternCompare(Pattern.JUMPING, x, y, z, s1, s2, s3, s4, xValue, yValue, zValue, s1Value, s2Value, s3Value, s4Value)) {
    			Message msg = camHandler.obtainMessage(CameraActivity.MESSAGE_TAKE_PIC);
    	        camHandler.sendMessage(msg);
            	isBelongAnyPattern = true;
            	Util.log("Jumping.");
            }
    	} else {
	    	if(patternCompare(Pattern.RUNNING, x, y, z, s1, s2, s3, s4, xValue, yValue, zValue, s1Value, s2Value, s3Value, s4Value)) {
	        	mUserStateControl.onStateChange(HoTDefine.State.RUNNING);
	        	isBelongAnyPattern = true;
	        	Util.log("Running.");
	        }
	    	
	    	if(!isBelongAnyPattern)
	        if(patternCompare(Pattern.WALKING, x, y, z, s1, s2, s3, s4, xValue, yValue, zValue, s1Value, s2Value, s3Value, s4Value)) {
	        	mUserStateControl.onStateChange(HoTDefine.State.WALKING);
	        	isBelongAnyPattern = true;
	        	Util.log("Walking.");
	        }
	    	if(!isBelongAnyPattern)
	    	if(patternCompare(Pattern.LEG_CROSSED, x, y, z, s1, s2, s3, s4, xValue, yValue, zValue, s1Value, s2Value, s3Value, s4Value)) {
	        	mUserStateControl.onStateChange(HoTDefine.State.LEG_CROSSED);
	        	isBelongAnyPattern = true;
	        	Util.log("Crossing leg.");
	        }
	        if(!isBelongAnyPattern)
	        if(patternCompare(Pattern.STANDING, x, y, z, s1, s2, s3, s4, xValue, yValue, zValue, s1Value, s2Value, s3Value, s4Value)) {
	        	mUserStateControl.onStateChange(HoTDefine.State.STANDING);
	        	isBelongAnyPattern = true;
	        	Util.log("Standing.");
	        }
	        if(!isBelongAnyPattern)
	        if(patternCompare(Pattern.SITTING, x, y, z, s1, s2, s3, s4, xValue, yValue, zValue, s1Value, s2Value, s3Value, s4Value)) {
	        	mUserStateControl.onStateChange(HoTDefine.State.SITTING);
	        	isBelongAnyPattern = true;
	        	Util.log("Sitting.");
	        }
    	}
        
        // Add more Pattern Comparing here.
        
        if(isBelongAnyPattern) {
        	//clearAllRecord();
        	clearRecordFrom(BluetoothLeService.SAMPLING_RATE / 2);
        }
    }
    
    private boolean compareValueRule(float[] recordValue, float[] patternValueLimit) {
        boolean result = true;
        int patternLength = patternValueLimit.length / 3;
        
        for(int i = 0;i < patternLength;i++) {
        	if(result) {
	            if(patternValueLimit[i*3] == LogicType.LESS_THAN) {
	            	result = recordValue[i] < patternValueLimit[i*3+2];
	            } else if(patternValueLimit[i*3] == LogicType.MORE_THAN) {
	            	result = recordValue[i] > patternValueLimit[i*3+2];
	            } else if(patternValueLimit[i*3] == LogicType.BE_EQUAL) {
	            	result = recordValue[i] == patternValueLimit[i*3+2];
	            } else if(patternValueLimit[i*3] == LogicType.LESS_THAN_INCLUDE) {
	            	result = recordValue[i] <= patternValueLimit[i*3+2];
	            } else if(patternValueLimit[i*3] == LogicType.MORE_THAN_INCLUDE) {
	            	result = recordValue[i] >= patternValueLimit[i*3+2];
	            } else if(patternValueLimit[i*3] == LogicType.MIDDLE) {
	            	result = (recordValue[i] < patternValueLimit[i*3+2] && recordValue[i] > patternValueLimit[i*3+1]);
	            }
        	}
        }
        return result;
    }
    
    // For Version 2.
    private boolean patternCompare(PatternDescription patternDef, byte[] x, byte[] y, byte[] z, byte[] s1, byte[] s2, byte[] s3, byte[] s4,
    		float[] xValue, float[] yValue, float[] zValue, float[] s1Value, float[] s2Value, float[] s3Value, float[] s4Value) {
    	boolean result = true;
    	
    	result = recordCourseAndValueCompare(x, patternDef.patterns.get(NikeSensorData.DescriptionLineType.X), 
    								         xValue, patternDef.valueLimit.get(NikeSensorData.DescriptionLineType.X_VALUE));
    	if(result)
    	result = recordCourseAndValueCompare(y, patternDef.patterns.get(NikeSensorData.DescriptionLineType.Y), 
		             						 yValue, patternDef.valueLimit.get(NikeSensorData.DescriptionLineType.Y_VALUE));
    	if(result)
    	result = recordCourseAndValueCompare(z, patternDef.patterns.get(NikeSensorData.DescriptionLineType.Z), 
	         								 zValue, patternDef.valueLimit.get(NikeSensorData.DescriptionLineType.Z_VALUE));
    	if(result)
    	result = recordCourseAndValueCompare(s1, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S1), 
		         							 s1Value, patternDef.valueLimit.get(NikeSensorData.DescriptionLineType.S1_VALUE));
    	if(result)
    	result = recordCourseAndValueCompare(s2, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S2), 
		         							 s2Value, patternDef.valueLimit.get(NikeSensorData.DescriptionLineType.S2_VALUE));
    	if(result)
    	result = recordCourseAndValueCompare(s3, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S3), 
		         							 s3Value, patternDef.valueLimit.get(NikeSensorData.DescriptionLineType.S3_VALUE));
    	if(result)
    	result = recordCourseAndValueCompare(s4, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S4), 
		         							 s4Value, patternDef.valueLimit.get(NikeSensorData.DescriptionLineType.S4_VALUE));
    	
    	return result;
    }
    
    // For Version 2.
    private boolean recordCourseAndValueCompare(byte[] recordCourse, byte[] coursePattern, float[] recordValue, float[] valuePattern) {
    	boolean result = true;
    	
    	result = recordCourseCompare(recordCourse, coursePattern);
    	if(valuePattern != null) {
    		if(result) {
    			result = compareValueRule(recordValue, valuePattern);
    		}
    	}
    	return result;
    }
    
    // Version 1: Only Compare line points Goes direction.
    private void compare(byte[] x, byte[] y, byte[] z, byte[] s1, byte[] s2, byte[] s3, byte[] s4) {
    	boolean isBelongAnyPattern = false;
    	
        if(patternCompare(Pattern.WALKING, x, y, z, s1, s2, s3, s4)) {
        	mUserStateControl.onStateChange(HoTDefine.State.WALKING);
        	isBelongAnyPattern = true;
        	//Util.log("Walking.");
        }
        if(patternCompare(Pattern.STANDING, x, y, z, s1, s2, s3, s4)) {
        	mUserStateControl.onStateChange(HoTDefine.State.STANDING);
        	isBelongAnyPattern = true;
        	//Util.log("Standing.");
        }
        
        // Add more Pattern Comparing here.
        
        if(isBelongAnyPattern) {
        	clearAllRecord();
        }
    }
    
    // For Version 1.
    private boolean patternCompare(PatternDescription patternDef, byte[] x, byte[] y, byte[] z, byte[] s1, byte[] s2, byte[] s3, byte[] s4) {
    	boolean result = true;
    	
    	result = recordCourseCompare(x, patternDef.patterns.get(NikeSensorData.DescriptionLineType.X));
    	if(result)
    		result = recordCourseCompare(y, patternDef.patterns.get(NikeSensorData.DescriptionLineType.Y));
    	if(result)
    		result = recordCourseCompare(z, patternDef.patterns.get(NikeSensorData.DescriptionLineType.Z));
    	if(result)
    		result = recordCourseCompare(s1, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S1));
    	if(result)
    		result = recordCourseCompare(s2, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S2));
    	if(result)
    		result = recordCourseCompare(s3, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S3));
    	if(result)
    		result = recordCourseCompare(s4, patternDef.patterns.get(NikeSensorData.DescriptionLineType.S4));
    	
    	return result;
    }
    
    // For Version 1.
    private boolean recordCourseCompare(byte[] recordCourse, byte[] coursePattern) {
    	boolean result = true;
    	
    	if(coursePattern != null) {
			byte[] courseRule = recordCourseTransfer(recordCourse);
			result = compareCourseRule(courseRule, coursePattern);
    	}
    	return result;
    }
    
    private byte[] recordCourseTransfer(byte[] record) {
    	byte[] rule = new byte[(record.length * 2)];
    	
    	byte dir = record[0];
    	byte count = 0;
    	int ruleWriteIndex = -2;
    	for(int i = 0;i < record.length;i++) {
            if(i == record.length - 1) {
            	if(dir == record[i]) {
                    count++;
                }
                ruleWriteIndex = ruleWriteIndex + 2;
                rule[ruleWriteIndex] = dir;
                rule[ruleWriteIndex+1] = count;
                count = 0;
                
                if(dir != record[i]) {
                    count++;
                    ruleWriteIndex = ruleWriteIndex + 2;
                    rule[ruleWriteIndex] = record[i];
                    rule[ruleWriteIndex+1] = count;
                    count = 0;
                }
            } else if(dir == record[i]) {
                count++;
            } else {
                ruleWriteIndex = ruleWriteIndex + 2;
                rule[ruleWriteIndex] = dir;
                rule[ruleWriteIndex+1] = count;
                count = 0;

                dir = record[i];
                count++;
            }
    	}
    	return rule;
    }
    
    private boolean compareCourseRule(byte[] recordRule, byte[] patternRule) {
        int patternLength = patternRule.length / 3;
        boolean result = true;
        
        for(int i = 0;i < patternLength;i++) {
            int j = 0;
            byte recordDir = recordRule[i*2];
            if(recordDir == 0) break;
            int length = recordRule[i*2+1];
            
            byte ruleDir = patternRule[i*3];
            int min = patternRule[i*3+1];
            int max = patternRule[i*3+2];
            
            if(recordDir == ruleDir) {
                if(!(length >= min && length <= max)) {
                    result = false;
                    break;
                }
            } else {
                result = false;
                break;
            }
        }
        return result;
    }
    
    private float[] getPointsRuleValue(PointsInfo[] pointsInfos) {
    	if(pointsInfos != null) {
            int length = pointsInfos.length;
            int valuesIndex = -1;
            float[] values = new float[length];
            int dir = -1;
            
            for(int i = 0;i < length;i++) {
                PointsInfo pointInfo = pointsInfos[i];
                
                if(dir == -1) {
                    dir = pointInfo.direction;
                } else if(i == length - 1) {
                    if(dir != pointInfo.direction) {
                        valuesIndex++;
                        values[valuesIndex] = pointsInfos[i-1].latestPoint;
                    }
                    valuesIndex++;
                    values[valuesIndex] = pointsInfos[i].latestPoint;
                } else if(dir != pointInfo.direction) {
                    dir = pointInfo.direction;
                    valuesIndex++;
                    values[valuesIndex] = pointsInfos[i-1].latestPoint;
                }
            }
            return values;
    	}
    	return null;
    }
    
    private void clearAllRecord() {
    	xAxisRecord.clear();
    	yAxisRecord.clear();
    	zAxisRecord.clear();
    	s1Record.clear();
    	s2Record.clear();
    	s3Record.clear();
    	s4Record.clear();
    }
    
    private void clearRecordFrom(int from) {
    	xAxisRecord.remove(from);
    	yAxisRecord.remove(from);
    	zAxisRecord.remove(from);
    	s1Record.remove(from);
    	s2Record.remove(from);
    	s3Record.remove(from);
    	s4Record.remove(from);
    }
}
