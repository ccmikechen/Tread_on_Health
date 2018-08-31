package edu.kuas.mis.wmc.service.client;

public class ToHRequestParams {
	public int seq;
	public int opCode;
	public String userID = null;
	public String name = null;
	public double longitude;
	public double latitude;
	public int userState;
	
	public ToHRequestParams(int seq, int opCode, String userID, double longitude, double latitude, int userState) {
		this.seq = seq;
		this.opCode = opCode;
		this.userID = userID;
		this.longitude = longitude;
		this.latitude = latitude;
		this.userState = userState;
	}
	
	public ToHRequestParams(int seq, int opCode, String userID, String name, double longitude, double latitude, int userState) {
		this(seq, opCode, userID, longitude, latitude, userState);
		this.name = name;
	}
}
