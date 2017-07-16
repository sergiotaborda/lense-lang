package lense.core.collections;


import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Native;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;


@Native(overridable = true)
@Signature("[+K<lense.core.lang.Any, +V<lense.core.lang.Any]::")
public class KeyValuePair extends Base {

	private Any value;
	private Any key;

	@Constructor
	public static KeyValuePair constructor (Any key, Any value){
		return new KeyValuePair(key, value);
	}
	
	private KeyValuePair (Any key, Any value){
		this.key = key;
		this.value = value;
	}
	
	public boolean equalsTo(Any other){
		return other instanceof KeyValuePair && this.equalsTo((KeyValuePair)other);
	}
	
	public boolean equalsTo(KeyValuePair other){
		return this.key.equalsTo(other.key) && this.value.equalsTo(other.value);
	}
	
	public lense.core.math.Integer hashValue(){
		return key.hashValue().plus(lense.core.math.Integer.valueOfNative(31).multiply(value.hashValue()));
	}
	
	@MethodSignature( returnSignature = "K" , paramsSignature = "")
	@Property(name = "key")
	public Any getKey(){
		return key;
	}
	
	@MethodSignature( returnSignature = "V" , paramsSignature = "")
	@Property(name = "value")
	public Any getValue(){
		return value;
	}
}
