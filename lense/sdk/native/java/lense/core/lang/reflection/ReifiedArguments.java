package lense.core.lang.reflection;

import lense.core.math.Natural;

public interface ReifiedArguments {

	public lense.core.lang.reflection.Type typeAt(Natural index);
	public ReifiedArguments fromIndex(Natural index);
}
