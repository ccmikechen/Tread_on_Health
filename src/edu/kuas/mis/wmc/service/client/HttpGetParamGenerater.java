package edu.kuas.mis.wmc.service.client;

import java.util.HashMap;

public class HttpGetParamGenerater {
	private HashMap<String, String> params;

	public HttpGetParamGenerater() {
		params = new HashMap();
	}
	
	public HttpGetParamGenerater add(String key, String value) {
		params.put(key, value);
		return this;
	}
	
	public String getURLString() {
		if(params != null) {
			boolean isFirst = true;
			StringBuilder result = new StringBuilder("?");
			for(Object key:params.keySet()) {
				if(!isFirst) {
					result.append("&");
				}
				result.append(key.toString()).append("=").append(params.get(key.toString()));
				
				if(isFirst) {
					isFirst = false;
				}
			}
			return result.toString();
		}
		return "";
	}
}
