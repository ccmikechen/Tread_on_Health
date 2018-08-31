package edu.kuas.mis.wmc.service.client;

import org.json.JSONException;
import org.json.JSONObject;

import edu.kuas.mis.wmc.app.Util;

public class ResponseErrorCodeJsonParser {
	private boolean isJson = false;
	private JSONObject jsonObj;
	
	public static String generateResponseString(ResponseErrorCode errorCode) {
		return "{\"ErrorCode\":\"" + errorCode.errorCode + "\"}";
	}
	
	public ResponseErrorCodeJsonParser(String jsonData) {
		try {
			this.jsonObj = new JSONObject(jsonData);
			isJson = true;
		} catch (JSONException e) {
			isJson = false;
		}
	}
	
	public ResponseErrorCode getErrorCode() {
		if(isJson) {
			String url = null;
			int ec = -1;
			try {
				if(jsonObj.getString("AddFriend") != null) {
					url = jsonObj.getString("AddFriend");
				}
			} catch (JSONException e) {
				
			} finally {
				try {
					ec = jsonObj.getInt("ErrorCode");
				} catch (JSONException je) {
					return new ResponseErrorCode(ResponseErrorCodeDefine.HAVE_NO_TITLE);
				}
				if(url != null) return new ResponseErrorCode(ec, url);
				return new ResponseErrorCode(ec);
			}
		} else 
			return new ResponseErrorCode(ResponseErrorCodeDefine.EXCEPTION);
	}
	
	
}
