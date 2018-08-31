package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NikeDatabase {

	private SQLiteDatabase db;
	
	public NikeDatabase(Context context, String path) throws SQLException {
		db = new SQLiteConnector().connect(context, path);
	}
	
	public void addData(String table, List<float[]> leftDataList, List<float[]> rightDataList) {
		float[][] leftData = leftDataList.toArray(new float[][]{});
		float[][] rightData = rightDataList.toArray(new float[][]{});

		Log.e("ADDDATA", String.valueOf(leftDataList.size()));

		addData(table, leftData, rightData);
	}
	
	public void addData(String table, float[][] leftData, float[][] rightData) {
		try {
			createDataTable();
			insertDatas(table, leftData, rightData);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createDataTable() throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS data")
		  .append("(name varchar(30),")
		  .append("time int,")
		  .append("lx float,")
		  .append("ly float,")
		  .append("lz float,")
		  .append("la float,")
		  .append("lb float,")
		  .append("lc float,")
		  .append("ld float,")
		  .append("rx float,")
		  .append("ry float,")
		  .append("rz float,")
		  .append("ra float,")
		  .append("rb float,")
		  .append("rc float,")
		  .append("rd float")
		  .append(")");
		db.execSQL(new String(sb));
	}
	
	private void insertDatas(String table, float[][] leftData, float[][] rightData) throws SQLException {
		int size = Math.min(leftData.length, rightData.length);
		for (int i = 0; i < size; i++)
			insertData(table, leftData[i], rightData[i], i);
	}
	
	private void insertData(String name, float[] leftData, float[] rightData, int count) throws SQLException {
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("time", count);
		values.put("lx", leftData[0]);
		values.put("ly", leftData[1]);
		values.put("lz", leftData[2]);
		values.put("la", leftData[3]);
		values.put("lb", leftData[4]);
		values.put("lc", leftData[5]);
		values.put("ld", leftData[6]);
		values.put("rx", rightData[0]);
		values.put("ry", rightData[1]);
		values.put("rz", rightData[2]);
		values.put("ra", rightData[3]);
		values.put("rb", rightData[4]);
		values.put("rc", rightData[5]);
		values.put("rd", rightData[6]);

		db.insert("data", null, values);
	}
	
	public float[][] getData(String name) throws SQLException {
		List<float[]> dataList = new ArrayList<float[]>();
		
		Cursor cursor = db.rawQuery("SELECT * FROM data WHERE name = '" + name + "' ORDER BY time", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			float[] data = new float[15];
			for (int i = 0; i < 15; i++)
				data[i] = cursor.getFloat(i+1);
			dataList.add(data);
			cursor.moveToNext();
		}
		return dataList.toArray(new float[][]{});
	}

	public String[] getTables() throws SQLException {
		List<String> tableList = new ArrayList<String>();

		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			tableList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		return tableList.toArray(new String[]{});
	}

	public boolean isDataExist(String name) {
		String s = "SELECT count(name) FROM data WHERE name = '" + name + "'";
		Cursor cursor = db.rawQuery(s, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		return count > 0;
	}

	public String[] getDataNames() {
		List<String> nameList = new ArrayList<String>();
		Cursor cursor = db.rawQuery("SELECT DISTINCT name FROM data", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			nameList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		return nameList.toArray(new String[]{});
	}
}
