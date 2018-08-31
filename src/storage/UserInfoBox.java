package storage;

import edu.kuas.mis.wmc.app.Util;
import android.content.Context;

public class UserInfoBox {
	public static final String PREFERENCES_NAME = "PREFERENCES_NAME";
	public static final String PREFERENCES_LINE_ID = "PREFERENCES_LINE_ID";
	public static final String PREFERENCES_GENDER = "PREFERENCES_GENDER";
	public static final String PREFERENCES_HEIGHT = "PREFERENCES_HEIGHT";
	public static final String PREFERENCES_WEIGHT = "PREFERENCES_WEIGHT";
	public static final String PREFERENCES_STEPS = "PREFERENCES_STEPS";
	public static final String PREFERENCES_POWER = "PREFERENCES_POWER";

	private PreferencesManager mPreferencesManager;
	
	public UserInfoBox(Context context) {
		this.mPreferencesManager = new PreferencesManager(context);
	}
	
	public void setName(String name) {
		mPreferencesManager.saveData(PREFERENCES_NAME, name);
	}
	
	public void setLineID(String id) {
		mPreferencesManager.saveData(PREFERENCES_LINE_ID, id);
	}
	
	public void setGender(String gender) {
		mPreferencesManager.saveData(PREFERENCES_GENDER, gender);
	}
	
	public void setHeight(String height) {
		mPreferencesManager.saveData(PREFERENCES_HEIGHT, height);
	}
	
	public void setWeight(String weight) {
		mPreferencesManager.saveData(PREFERENCES_WEIGHT, weight);
	}
	
	public void setSteps(String steps) {
		mPreferencesManager.saveData(PREFERENCES_STEPS, steps);
	}
	
	public void setPower(int power) {
		mPreferencesManager.saveData(PREFERENCES_POWER, String.valueOf(power));
	}
	
	public String getName() {
		return mPreferencesManager.restoreData(PREFERENCES_NAME);
	}
	
	public String getLineID() {
		return mPreferencesManager.restoreData(PREFERENCES_LINE_ID);
	}
	
	public String getGender() {
		return mPreferencesManager.restoreData(PREFERENCES_GENDER);
	}
	
	public String getHeight() {
		return mPreferencesManager.restoreData(PREFERENCES_HEIGHT);
	}
	
	public String getWeight() {
		return mPreferencesManager.restoreData(PREFERENCES_WEIGHT);
	}
	
	public String getSteps() {
		return mPreferencesManager.restoreData(PREFERENCES_STEPS);
	}
	
	public int getPower() {
		return Integer.parseInt(mPreferencesManager.restoreData(PREFERENCES_POWER));
	}
}
