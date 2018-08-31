package data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NikeDataPlayer implements Runnable {

	private long delayTime = 1000/16; //Default 16hz
	
	private float[][] leftData;
	private float[][] rightData;
	
	private int currentTime = 0;
	private boolean isStarted = false;
	private boolean isRunning = false;
	
	private DataGetterInterface leftDataGetter = null;
	private DataGetterInterface rightDataGetter = null;
	
	private List<PlayTimeListener> playTimeListenerList = null;
	
	private Thread thread = null;
	
	public NikeDataPlayer(NikeDataLoader dataLoader) {
		this.leftData = dataLoader.getLeftData();
		this.rightData = dataLoader.getRightData();
		this.playTimeListenerList = new ArrayList<PlayTimeListener>();
	}
	
	public void start() {
		if (this.thread == null) {
			this.isRunning = true;
			this.thread = new Thread(this);
			this.thread.start();
		}
		this.isStarted = true;
	}
	
	public void stop() {
		this.isStarted = false;
	}
	
	public void close() {
		this.isRunning = false;
		this.thread = null;
	}
	
	public int getLength() {
		return leftData.length;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(int time) {
		currentTime = time < 0? 0 : time > getLength()? getLength() : time;
	}
	
	public void setDataGetter(DataGetterInterface leftDataGetter, DataGetterInterface rightDataGetter) {
		this.leftDataGetter = leftDataGetter;
		this.rightDataGetter = rightDataGetter;
	}
	
	public void addPlayTimeListener(PlayTimeListener playTimeListener) {
		this.playTimeListenerList.add(playTimeListener);
	}

	/***
	 * @param hz 每秒播放資料數
	 */
	public void setPlaySpeed(int hz) {
		this.delayTime = 1000/hz;
	}

	public void run() {
		while (isRunning) {
			int realCurrentTime;
			if ((realCurrentTime = currentTime) < getLength()) {
				this.leftDataGetter.dataCallBack(leftData[realCurrentTime]);
				this.rightDataGetter.dataCallBack(rightData[realCurrentTime]);
				
				if (isStarted) {
					notifyAllPlayTimeListener();
					currentTime++;
					try {
						TimeUnit.MILLISECONDS.sleep(delayTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void notifyAllPlayTimeListener() {
		new Thread() {
			public void run() {
				for (PlayTimeListener listener : playTimeListenerList)
					listener.tick(currentTime);
			}
		}.start();
	}
}
