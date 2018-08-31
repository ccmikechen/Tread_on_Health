package edu.kuas.mis.wmc.service.client;

public class HoTDefine {
	public class Abbr {
		public static final String SEQ = "s";
		public static final String OPCODE = "c";
		public static final String USER_ID = "i";
		public static final String NAME = "n";
		public static final String LONGTITUDE = "lv";
		public static final String LATITUDE = "lh";
		public static final String STATE = "a";
	}
	
	public class OPCode {
		public static final int UPDATE_STATE = 0;
		public static final int REQ_PAIR = 1;
		public static final int WAIT_PAIR = 2;
		public static final int PAIR_OR_WAIT = 3;
		public static final int USER_CHECK = 4;
	}
	
	public class State {
		public static final int STANDING = 0;
		public static final int WALKING = 1;
		public static final int RUNNING = 2;
		public static final int LEG_CROSSED = 3;
		public static final int JUMPING = 4;
		public static final int SITTING = 5;
		public static final int UNKNOW = 256;
		
		public static final String STANDING_STR = "站立";
		public static final String WALKING_STR = "走路";
		public static final String RUNNING_STR = "跑步";
		public static final String LEG_CROSSED_STR = "翹腳";
		public static final String JUMPING_STR = "跳";
		public static final String SITTING_STR = "坐";
		public static final String UNKNOW_STR = "";//無法辨識
	}
}
