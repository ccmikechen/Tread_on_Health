package data;

import java.util.ArrayList;
import java.util.List;

public class DataRecorder implements DataGetterInterface {

	private List<float[]> dataList;
	private boolean isStarted = false;
	
	public DataRecorder() {
		dataList = new ArrayList<float[]>();
	}
	
	@Override
	public void dataCallBack(float[] signals) {
		if (isStarted)
			dataList.add(signals);
	}

	public void start() {
		dataList.clear();
		isStarted = true;
	}
	
	public void stop() {
		isStarted = false;
	}
	
	public List<float[]> getData() {
		return new ArrayList<float[]>(dataList);
	}
}
