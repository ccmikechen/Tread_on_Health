package storage;

import edu.kuas.mis.wmc.app.Util;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
	private Context mContext;
	private SharedPreferences settings;
	
	public PreferencesManager(Context context) {
		this.mContext = context;
		sharedPreferencesInit();
	}
	
	public boolean saveData(Object key, Object value) {
		try {
			if(settings != null) {
				boolean result = settings.edit().putString(key.toString(), value.toString()).commit();
				return result;
			}
		} catch(Exception e) {
			Util.log("PreferencesManager.saveData() error: " + e.getMessage());
		}
		return false;
	}
	
	public String restoreData(Object key) {
		if(settings != null) 
			return settings.getString(key.toString(), "");
		else
			return "";
	}
	
	private void sharedPreferencesInit() {
		this.settings = this.mContext.getSharedPreferences("PreferenceData", Context.MODE_PRIVATE);
	}
}
