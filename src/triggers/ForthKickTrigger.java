package triggers;
import android.util.Log;

import data.NikeDataGetter;


public class ForthKickTrigger extends Trigger {

	private NikeDataGetter getter = null;
	private boolean isKicked = false;
	
	private float y = 0f;
	
	public ForthKickTrigger(NikeDataGetter getter) {
		this.getter = getter;
	}
	
	@Override
	protected boolean triggered() {
		y = getter.getY();
		return !isKicked && y > 6.0;
	}

	@Override
	protected void doTriggeredAction() {
		Log.e(TAG, "Forth Kick!");
		isKicked = true;
	}
	
	@Override
	protected void doNoTriggeredAction() {
		if (y < 6.0)
			isKicked = false;
	}

}
