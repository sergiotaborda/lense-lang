package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public interface SmallArray {

	public int size();
	public Any getAtPrimitiveIndex(int index);
	public void setAtPrimitiveIndex(int index , Any value);
}
