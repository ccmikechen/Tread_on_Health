package triggers;
import android.util.Log;

import data.NikeDataGetter;

public class FootBackPressTrigger extends Trigger {
	
	private NikeDataGetter getter = null;
	private int note = 0;
	
	private boolean isPressed = false;
	
	private float z = 0.0f;
	
	public FootBackPressTrigger(NikeDataGetter getter, int note) {
		this.getter = getter;
		this.note = note;
	}
	
	@Override
	protected boolean triggered() {
		z = getter.getZ();
		
		return !isPressed && z > 1.5;
	}

	@Override
	protected void doTriggeredAction() {
		Log.e(TAG, "Foot Back Pressed!");
		isPressed = true;
	}
	
	@Override
	protected void doNoTriggeredAction() {
		if (isPressed && z < 1.5) {
			isPressed = false;
		}
	}

}
