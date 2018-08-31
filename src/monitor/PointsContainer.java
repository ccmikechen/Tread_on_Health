package monitor;
public class PointsContainer {
    private PointsRecognizer[] queue;
    private int count;
    private int monitorNumber;
    
    public PointsContainer(int monitorNumber) {
    	init(monitorNumber);
    }
    
    public boolean add(PointsRecognizer lineRecognizer) {
        if(count <= monitorNumber) {
            move();
            offer(lineRecognizer);
            return true;
        } else {
            return false;
        }
    }
	
    private void move() {
        for(int i = 0;i < (monitorNumber - 1);i++) {
            queue[i] = queue[i+1];
        }
        queue[monitorNumber-1] = null;
    }
    
    public PointsRecognizer getPoint(int index) {
        return queue[index];
    }
    
    public PointsRecognizer[] getAllPoint() {
        return queue;
    }
	
    public int getCount() {
        return count;
    }
    
    public void removeAll() {
    	init(this.monitorNumber);
    }
    
    public void remove(int from) {
    	PointsRecognizer[] queueTmp = queue;
    	removeAll();
    	for(int i = from;from < queueTmp.length;i++) {
    		add(queueTmp[i]);
    	}
    }
    
    private void init(int monitorNumber) {
    	this.queue = new PointsRecognizer[monitorNumber];
        this.count = 0;
        this.monitorNumber = monitorNumber;
    }
    
    private void offer(PointsRecognizer lineRecognizer) {
        queue[monitorNumber-1] = lineRecognizer;
        if(count < monitorNumber) count++;
    }
}
