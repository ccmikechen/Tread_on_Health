package monitor.structure;

import edu.kuas.mis.wmc.service.client.HoTDefine;
import bluetooth.le.BluetoothLeService;

public class Pattern {
	public static final byte END_NUMBER = (byte)BluetoothLeService.SAMPLING_RATE;
    /* Length: 3 * X
     * Format: Course, MIN, MAX, Course, MIN, MAX, ...
     *         Type, value,      Type, value,      ...
     */
    public static final PatternDescription WALKING = new PatternDescription(
    		HoTDefine.State.WALKING, 
			null, null, 
			null, null, 
			null, null,
			null, null, 
			null, null, 
			null, null, 
			new byte[]{(byte)CourseType.DOWN, 0x01, 0x03, (byte)CourseType.KEEP, 0x03, 0x0C, (byte)CourseType.UP, 0x01, END_NUMBER}, 
			new float[]{LogicType.LESS_THAN, 0, 3, LogicType.LESS_THAN, 0, 3, LogicType.MORE_THAN, 0, 3}
		);
    
    public static final PatternDescription STANDING = new PatternDescription(
    		HoTDefine.State.STANDING, 
    		new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER}, 
    		null,
    		new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER}, 
    		null,
    		new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER}, 
    		new float[]{LogicType.LESS_THAN, 0, -0.5f}, 
			null, null, 
			null, null, 
			null, null, 
			null, 
			new float[]{LogicType.MORE_THAN, 0, 3f}
		);
    
    public static final PatternDescription RUNNING = new PatternDescription(
    		HoTDefine.State.RUNNING, 
			null, null, 
			new byte[]{(byte)CourseType.DOWN, 0x01, END_NUMBER}, 
			null, 
			null, null,
			null, null, 
			null, null, 
			null, null, 
			new byte[]{(byte)CourseType.DOWN, 0x01, 0x01, (byte)CourseType.KEEP, 0x02, 0x09, (byte)CourseType.UP, 0x01, END_NUMBER}, 
			new float[]{LogicType.LESS_THAN, 0, 3, LogicType.LESS_THAN, 0, 3, LogicType.MORE_THAN, 0, 3}
		);
    
    public static final PatternDescription LEG_CROSSED = new PatternDescription(
    		HoTDefine.State.LEG_CROSSED, 
			null, 
			new float[]{LogicType.MORE_THAN, 0, 0.2f}, 
			null, null,
			null, 
			new float[]{LogicType.MIDDLE, -0.8f, 0.3f},
			new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER}, 
			new float[]{LogicType.LESS_THAN, 0, 4},
			new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER}, 
			new float[]{LogicType.LESS_THAN, 0, 4},
			new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER}, 
			new float[]{LogicType.LESS_THAN, 0, 4},
			new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER}, 
			new float[]{LogicType.LESS_THAN, 0, 4}
		);
    
    public static final PatternDescription JUMPING = new PatternDescription(
    		HoTDefine.State.JUMPING, 
			null, null, 
			new byte[]{(byte)CourseType.UP, 0x01, 0x03, (byte)CourseType.DOWN, 0x02, 0x03, (byte)CourseType.UP, 0x01, END_NUMBER}, 
			new float[]{LogicType.MORE_THAN, 0, 2.5f, LogicType.LESS_THAN, 0, -1f},
			null, null,
			null, null,
			null, null,
			null, null,
			null, null
		);
    
    public static final PatternDescription SITTING = new PatternDescription(
    		HoTDefine.State.SITTING, 
    		null, null,
    		null, null,
    		null, 
    		new float[]{LogicType.LESS_THAN, 0, -0.5f},
    		null,
    		new float[]{LogicType.MORE_THAN, 0, 5f},
			null, null,
			null, null,
			new byte[]{(byte)CourseType.KEEP, (byte)BluetoothLeService.SAMPLING_RATE, END_NUMBER},
			new float[]{LogicType.LESS_THAN, 0, 5f}
		);
}
