package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Signature;
import lense.core.lang.java.ValueClass;

@Signature ("::lense.core.lang.ExclusiveDijunctable<HashValue>")
@ValueClass
public final class HashValue extends Base implements ExclusiveDijunctable, AnyValue {

    private int code;
    
    public static HashValue fromPrimitive(int value){
        return new HashValue(value);
    }
    
    @Constructor(paramsSignature = "")
    public static HashValue constructor(){
        return new HashValue(0);
    }
    
    public HashValue(int code) {
        this.code = code;
    }

    @MethodSignature(returnSignature = "lense.core.lang.HashValue" , paramsSignature = "lense.core.lang.HashValue")
    public HashValue concat(HashValue other){
        return new HashValue(code + 31 * other.code);
    }

    @MethodSignature(returnSignature = "lense.core.lang.HashValue" , paramsSignature = "lense.core.lang.HashValue")
    @Override
    public HashValue xor(Any other) {
        if (other instanceof HashValue){
            return new HashValue(this.code ^ ((HashValue)other).code);
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }
    
    public java.lang.String toString() {
    	return java.lang.String.valueOf(code);
    }
    
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof HashValue hash && this.code == hash.code; 
	}
	
    @PlatformSpecific
    public int hashCode(){
        return code;
    }
    
    @Override
    public HashValue hashValue() {
        return this;
    }
    
}
