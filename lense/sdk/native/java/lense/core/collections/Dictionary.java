package lense.core.collections;

import java.util.HashMap;
import java.util.Map;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.Maybe;
import lense.core.lang.None;
import lense.core.lang.Some;
import lense.core.lang.String;
import lense.core.lang.java.Base;
import lense.core.lang.reflection.JavaReifiedArguments;
import lense.core.lang.reflection.ReifiedArguments;
import lense.core.lang.reflection.TypeResolver;
import lense.core.math.Natural;
import lense.core.math.Natural64;

public class Dictionary extends Base implements Association {

	public static Dictionary fromKeyValueArray(ReifiedArguments args, KeyValuePair ... pairs){
		return new Dictionary( args,pairs);
		
	}
	
	private Map<Any,Any> map = new HashMap<>();
	private ReifiedArguments args;
	
	public Dictionary(ReifiedArguments args,KeyValuePair[] pairs) {
		this.args = args;
		for(KeyValuePair pair : pairs){
			map.put(pair.getKey(), pair.getValue());
		}
	}

	@Override
	public boolean contains(Any other) {
		return map.containsKey(other);
	}

	@Override
	public boolean containsAll(Assortment other) {
		Iterator it = other.getIterator();
		while(it.moveNext()){
			if (!this.contains(it.current())){
				return false;
			}
		}
		return true;
	}

	@Override
	public String asString() {
		return String.valueOfNative(map.toString());
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Dictionary && equals((Dictionary)other);
	}
	
	public boolean equalsTo(Dictionary other) {
		return this.map.equals(other.map);
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(map.hashCode());
	}

	@Override
	public Iterator getIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Natural getSize() {
		return Natural64.valueOfNative(map.size());
	}

	@Override
	public boolean getEmpty() {
		return map.isEmpty();
	}

	@Override
	public Maybe get(Any key) {
		Any value = map.get(key);
		if (value == null){
			return None.NONE;
		} else {
			return Some.constructor(JavaReifiedArguments.getInstance().addType(args.typeAt(Natural64.ONE)) , value); 
		}
	}

}
