package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Base;
import lense.core.lang.java.Native;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Native
public class NativeNaturalProgression extends Base implements Progression{

	private Natural start;
	Natural end;

	public NativeNaturalProgression(Natural start, Natural end){
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Iterator getIterator() {
		return new NativeIterator(this, start);
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeNaturalProgression && ((NativeNaturalProgression)other).start == this.start && ((NativeNaturalProgression)other).end == this.end;
	}

	@Override
	public Integer hashValue() {
		return start.hashValue().plus(end.hashValue()); // TODO xor
	}
}
