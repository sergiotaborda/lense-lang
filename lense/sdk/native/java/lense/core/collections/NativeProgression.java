package lense.core.collections;

import java.util.stream.IntStream;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Base;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

public class NativeProgression extends Base implements Progression{

	private int start;
	private int end;

	public NativeProgression(int start, int end){
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Iterator getIterator() {
		return new IteratorAdapter(IntStream.range(start, end + 1).mapToObj(i -> Natural.valueOfNative(i)).iterator());
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeProgression && ((NativeProgression)other).start == this.start && ((NativeProgression)other).end == this.end;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(start ^ end);
	}
}
