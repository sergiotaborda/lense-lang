package lense.core.collections;

import lense.core.lang.Any;
import lense.core.math.Natural;

interface ArrayStrategy {

	public Array createArrayFrom(Natural size, Any seed);

	public Array createArrayFrom(Any[] arrayOfAny);

	public Array createArrayFrom(Sequence seq);

	public Array createEmpty();

}
