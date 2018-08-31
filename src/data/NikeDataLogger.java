package data;

import android.util.Log;

import java.sql.SQLException;
import java.util.List;

public class NikeDataLogger {

	private NikeDatabase database = null;
	private DataRecorder leftDataRecorder = null;
	private DataRecorder rightDataRecorder = null;
	
	public NikeDataLogger(NikeDatabase database, DataRecorder leftDataRecorder, DataRecorder rightDataRecorder) {
		this.database = database;
		this.leftDataRecorder = leftDataRecorder;
		this.rightDataRecorder = rightDataRecorder;
	}
	
	public void start() {
		this.leftDataRecorder.start();
		this.rightDataRecorder.start();
	}
	
	public void stop() {
		this.leftDataRecorder.stop();
		this.rightDataRecorder.stop();
	}
	
	public boolean log(String name) {
		Log.e("DB", "Start logging to database");

		if (database.isDataExist(name))
			return false;

		List<float[]> leftDataList = leftDataRecorder.getData();
		List<float[]> rightDataList = rightDataRecorder.getData();
		this.database.addData(name, leftDataList, rightDataList);

		Log.e("DB", "Logged to database");


		return true;
	}
}
