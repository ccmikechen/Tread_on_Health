package triggers;
import android.util.Log;

import data.NikeDataGetter;


public class LeftRightSwingTrigger extends Trigger {
	
	private NikeDataGetter getter = null;
	private long startTime = 0;
	private long startPlayingTime = 0;
	private boolean isStarted = false;
	private float x = 0f;
	
	public LeftRightSwingTrigger(NikeDataGetter getter) {
		this.getter = getter;	}
	
	@Override
	protected boolean triggered() {
		x = getter.getX();
		return x > 1.2 || x < -1.2;
	}

	@Override
	protected void doTriggeredAction() {
		if (!isStarted) {
			Log.e(TAG, "Start Left Right Swing!");
			isStarted = true;
			startPlayingTime = System.nanoTime();
		}
		
		checkShaking();
		startTime = System.nanoTime();
	}
	
	@Override
	protected void doNoTriggeredAction() {
		if (isStarted) {
			if (System.nanoTime() - startTime > 3e8) {
				Log.e(TAG, "Stop Left Right Swing!");
				isStarted = false;
			}
		}
	}

	private void checkShaking() {
		if (System.nanoTime() - startPlayingTime > 5e7) {
			startPlayingTime = System.nanoTime();
		}
	}
	
}
