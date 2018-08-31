package triggers;

public class PressTrigger extends Trigger {

	private PressDataGetter getter = null;
	private int note = 0;
	private boolean isPressed = false;
	private float data = 0.0f;
	
	public PressTrigger(PressDataGetter getter, int note) {
		this.getter = getter;
		this.note = note;
	}
	
	@Override
	protected boolean triggered() {
		data = getter.getData();
		return data > 5000;
	}

	@Override
	protected void doTriggeredAction() {
		if (!isPressed) {
			int intensity = data > 200000? 120 : (int) (data * 120 / 200000);

			isPressed = true;
		}
	}

	@Override
	protected void doNoTriggeredAction() {
		if (isPressed) {

			isPressed = false;
		}
	}

}
