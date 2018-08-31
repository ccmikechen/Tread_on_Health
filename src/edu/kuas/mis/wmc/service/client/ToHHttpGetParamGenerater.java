package edu.kuas.mis.wmc.service.client;

public class ToHHttpGetParamGenerater extends HttpGetParamGenerater{
	public ToHHttpGetParamGenerater(ToHRequestParams params) {
		add(HoTDefine.Abbr.SEQ, String.valueOf(params.seq));
		add(HoTDefine.Abbr.OPCODE, String.valueOf(params.opCode));
		add(HoTDefine.Abbr.USER_ID, String.valueOf(params.userID));
		if(params.name != null) add(HoTDefine.Abbr.NAME, String.valueOf(params.name));
		add(HoTDefine.Abbr.LONGTITUDE, String.valueOf(params.longitude));
		add(HoTDefine.Abbr.LATITUDE, String.valueOf(params.latitude));
		add(HoTDefine.Abbr.STATE, String.valueOf(params.userState));
	}
}
