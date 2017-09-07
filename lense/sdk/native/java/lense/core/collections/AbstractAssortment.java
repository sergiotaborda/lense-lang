package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.PlataformSpecific;
import lense.core.lang.java.Signature;

@Signature("[+T<lense.core.lang.Any]::lense.core.collections.Assortment<T>")
@PlataformSpecific
public abstract class AbstractAssortment extends Base implements Assortment {

	public lense.core.lang.String asString(){
		Iterator it = this.getIterator();
		lense.core.lang.String result = lense.core.lang.String.valueOfNative("[");
		boolean isFirst = true;
		
		while(it.moveNext()){
			if (!isFirst){
				result= result.plus(lense.core.lang.String.valueOfNative(","));
			} else {
				isFirst = false;
			}
			
			Any value = it.current();
			
			result = result.plus(value.asString());
		
		}
		result = result.plus(lense.core.lang.String.valueOfNative("]"));
		return result;
	}
}
