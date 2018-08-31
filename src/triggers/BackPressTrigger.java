package triggers;
import android.util.Log;

import data.NikeDataGetter;


public class BackPressTrigger extends Trigger {
	
	private NikeDataGetter getter = null;

	private boolean isPressed = false;
	
	private float d = 0f;
	private float y = 0f;
	private float z = 0f;
	private float g = 0f;
	
	public BackPressTrigger(NikeDataGetter getter) {
		this.getter = getter;
	}
	
	@Override
	protected boolean triggered() {
		d = getter.getD();
		y = getter.getY();
		z = getter.getZ();
		g = getter.getG();
		
		return !isPressed &&
				d > 10 &&
				g > 1.0 &&
				z < 0.0 &&
				(y > 1.0 || y < -0.2);
	}

	@Override
	protected void doTriggeredAction() {
		Log.e(TAG, "Back Pressed!");
		isPressed = true;
	}
	
	@Override
	protected void doNoTriggeredAction() {
		if (isPressed && d < 10) {
			isPressed = false;
		}
	}

}
