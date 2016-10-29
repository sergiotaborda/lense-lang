package lense.core.collections;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Base;
import lense.core.lang.java.Native;
import lense.core.math.Int32;
import lense.core.math.Integer;

@Native
public class NativeBigIntegerProgression extends Base implements Progression{

	private BigInteger start;
	BigInteger end;
	BigInteger step;

	public NativeBigIntegerProgression(BigInteger start, BigInteger end , BigInteger step){
		this.start = start;
		this.end = end;
		this.step = step;
	}
	
	@Override
	public Iterator getIterator() {
		return new BigIterator(this, start);
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeBigIntegerProgression && ((NativeBigIntegerProgression)other).start.compareTo(this.start) == 0 && ((NativeBigIntegerProgression)other).end.compareTo(this.end) == 0;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(start.hashCode() ^ end.hashCode());
	}
}
