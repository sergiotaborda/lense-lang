package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.Ordinal;
import lense.core.lang.java.Base;
import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public class NativeOrdinalProgression extends Base implements Progression{

	private Ordinal start;
    Ordinal end;
    boolean includeEnd;

	public NativeOrdinalProgression(Ordinal start, Ordinal end, boolean includeEnd){
		this.start = start;
		this.end = end;
		this.includeEnd = includeEnd;
	}
	
	@Override
	public Iterator getIterator() {
		return new NativeOrdinalIterator(this, start);
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeOrdinalProgression 
		        && ((NativeOrdinalProgression)other).start == this.start 
		        && ((NativeOrdinalProgression)other).end == this.end;
	}

	@Override
	public HashValue hashValue() {
		return start.hashValue().concat(end.hashValue()); 
	}
}
