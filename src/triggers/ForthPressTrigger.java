package triggers;
import android.util.Log;

import data.NikeDataGetter;

public class ForthPressTrigger extends Trigger {
	
	private NikeDataGetter getter = null;

	private boolean isPressed = false;
	
	private float a = 0f;
	private float y = 0f;
	private float g = 0f;
	
	public ForthPressTrigger(NikeDataGetter getter) {
		this.getter = getter;
	}
	
	@Override
	protected boolean triggered() {
		a = getter.getA();
		y = getter.getY();
		g = getter.getG();
		
		return !isPressed &&
				a > 10 &&
				g > 1.0 &&
				(y < -1.0 || y > 0.2);
	}

	@Override
	protected void doTriggeredAction() {
		Log.e(TAG, "Forth Pressed!");
		isPressed = true;
	}
	
	@Override
	protected void doNoTriggeredAction() {
		if (isPressed && a < 10) {
			isPressed = false;
		}
	}

}
