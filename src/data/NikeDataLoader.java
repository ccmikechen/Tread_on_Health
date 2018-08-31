package data;

import java.sql.SQLException;

public class NikeDataLoader {

	private NikeDatabase database = null;
	private float[][] data = null;
	
	public NikeDataLoader(NikeDatabase database) {
		this.database = database;
	}
	
	public void loadData(String dataName) throws NotFoundRecordException {
		try {
			data = database.getData(dataName);
		} catch (SQLException e) {
			throw new NotFoundRecordException(e);
		}
	}
	
	public float[][] getLeftData() {
		float[][] leftData = new float[data.length][7];
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < 7; j++)
				leftData[i][j] = data[i][j+1];
		return leftData;
	}
	
	public float[][] getRightData() {
		float[][] rightData = new float[data.length][7];
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < 7; j++)
				rightData[i][j] = data[i][j+8];
		return rightData;
	}
}
