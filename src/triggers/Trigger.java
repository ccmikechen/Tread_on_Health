package triggers;

public abstract class Trigger implements Runnable {

	public final static String TAG = "Trigger";

	abstract protected boolean triggered();
	abstract protected void doTriggeredAction();
	abstract protected void doNoTriggeredAction();
	
	public void run() {
		while (true) {
			if (triggered()) 
				doTriggeredAction();
			else
				doNoTriggeredAction();
		}
	}
	
}
