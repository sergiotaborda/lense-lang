package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.NativeString;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Signature;

@Signature("[+T<lense.core.lang.Any]::lense.core.collections.Assortment<T>")
@PlatformSpecific
public abstract class AbstractAssortment extends Base implements Assortment {

	public lense.core.lang.String asString(){
		Iterator it = this.getIterator();
		lense.core.lang.String result = NativeString.valueOfNative("[");
		boolean isFirst = true;
		
		while(it.moveNext()){
			if (!isFirst){
				result= result.concat(NativeString.valueOfNative(","));
			} else {
				isFirst = false;
			}
			
			Any value = it.current();
			
			result = value == null ? result.concat("null") : result.concat(value.asString());
		
		}
		result = result.concat(NativeString.valueOfNative("]"));
		return result;
	}

}
