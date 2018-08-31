package data;

import java.sql.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class SQLiteConnector {

	class SQLiteHelper extends SQLiteOpenHelper {


		public SQLiteHelper(Context context, String dbName) {
			super(context, dbName, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

		}
	}

	public SQLiteDatabase connect(Context context, String dbName) throws SQLException {
		return new SQLiteHelper(context, dbName).getWritableDatabase();
	}

}
