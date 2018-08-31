package edu.kuas.mis.wmc.service.client;

import edu.kuas.mis.wmc.app.Util;

public class ResponseErrorCode {
	public int errorCode = -1;
	public String friendID = null;
	
	public ResponseErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public ResponseErrorCode(int errorCode, String friendID) {
		this.errorCode = errorCode;
		this.friendID = friendID;
	}
}
