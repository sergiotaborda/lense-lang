package lense.core.collections;

import java.util.stream.IntStream;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Base;
import lense.core.math.Natural64;

public class NativeProgression extends Base implements Progression{

	private int start;
	private int end;

	public NativeProgression(int start, int end){
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Iterator getIterator() {
		return new IteratorAdapter(IntStream.range(start, end + 1).mapToObj(i -> Natural64.valueOfNative(i)).iterator());
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeProgression && ((NativeProgression)other).start == this.start && ((NativeProgression)other).end == this.end;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(start ^ end);
	}
}
