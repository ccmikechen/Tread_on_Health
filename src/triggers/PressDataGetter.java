package triggers;
import data.NikeDataGetter;


public class PressDataGetter {

	public final static int A = 1;
	public final static int B = 2;
	public final static int C = 3;
	public final static int D = 4;
	
	private NikeDataGetter getter = null;
	private int position;
	
	public PressDataGetter(NikeDataGetter getter, int position) {
		this.getter = getter;
		this.position = position;
	}
	
	public float getData() {
		switch(position) {
		case A : return getter.getA();
		case B : return getter.getB();
		case C : return getter.getC();
		case D : return getter.getD();
		}
		return 0;
	}
	
}
