package triggers;
import android.util.Log;

import data.NikeDataGetter;


public class ForthBackSwingTrigger extends Trigger {
	
	private NikeDataGetter getter = null;
	private long startTime = 0;
	private long startPlayingTime = 0;
	private boolean isStarted = false;
	private float a = 0f;
	private float d = 0f;
	private float y = 0f;
	
	public ForthBackSwingTrigger(NikeDataGetter getter) {
		this.getter = getter;	}
	
	@Override
	protected boolean triggered() {
		a = getter.getA();
		d = getter.getD();
		y = getter.getY();
		return (y > 2.0 || (isStarted && y < -0.5)) &&
			   a < 5 && d < 5;
	}

	@Override
	protected void doTriggeredAction() {
		if (!isStarted) {
			Log.e(TAG, "Start Forth Back Swing!");
			isStarted = true;
			startPlayingTime = System.nanoTime();
		}
		
		checkAndPlayShakingSound();
		startTime = System.nanoTime();
	}
	
	@Override
	protected void doNoTriggeredAction() {
		
		if (isStarted) {
			if (System.nanoTime() - startTime > 2e8 ||
				a > 5 || d > 5) {
				Log.e(TAG, "Stop Forth Back Swing!");
				isStarted = false;
			}
		}
	}

	private void checkAndPlayShakingSound() {
		if (System.nanoTime() - startPlayingTime > 5e7) {
			startPlayingTime = System.nanoTime();
		}
	}
	
}
