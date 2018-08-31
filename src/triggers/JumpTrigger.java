package triggers;
import data.NikeDataGetter;

public class JumpTrigger extends Trigger {

	private NikeDataGetter getter = null;

	private boolean isStart = false;
	private boolean isJumping = false;
	private long startTime = 0;
	private float maxZ = 0f;

	private float a = 0f;
	private float z = 0f;

	public JumpTrigger(NikeDataGetter getter) {
		this.getter = getter;	}

	@Override
	protected boolean triggered() {
		a = getter.getA();
		z = getter.getZ();

		if (isStart)
			return System.nanoTime() - startTime < 1e9;
		return a > 500000;
	}

	@Override
	protected void doTriggeredAction() {
		if (isStart == false) {
			startTime = System.nanoTime();
			isStart = true;
		} else if (isJumping == false) {
			if (z < -2.0f) {
				maxZ = z;
				isJumping = true;
			}
		} else {
			if (z < maxZ && z > 0f) {
				//System.out.println("Jumped");
				isStart = false;
			} else {
				maxZ = z;
			}
		}

	}

	@Override
	protected void doNoTriggeredAction() {
		isStart = false;
		isJumping = false;
	}

}
