package triggers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TriggersManager {

	ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public void addTrigger(Trigger trigger) {
		threadPool.execute(trigger);
		
	}
	
	public void closeAllTriggers() {
		threadPool.shutdown();
	}
	
}
