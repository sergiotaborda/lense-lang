package lense.core.collections;

import java.util.stream.IntStream;

import lense.core.math.Natural;

public class NativeProgression implements Progression{

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

}
