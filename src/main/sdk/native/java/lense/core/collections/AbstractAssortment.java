package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.Native;

@Native
public abstract class AbstractAssortment extends Base implements Assortment {

	public lense.core.lang.String asString(){
		Iterator it = this.getIterator();
		lense.core.lang.String result = lense.core.lang.String.valueOfNative("[");
		
		while(it.hasNext()){
			Any value = it.next();
			
			result = result.plus(value.asString());
		
			if (it.hasNext()){
				result= result.plus(lense.core.lang.String.valueOfNative(","));
			}
		}
		result = result.plus(lense.core.lang.String.valueOfNative("]"));
		return result;
	}
}
