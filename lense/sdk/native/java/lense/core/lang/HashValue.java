package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Signature;

@Signature ("::lense.core.lang.ExclusiveDijunctable<HashValue>")
public final class HashValue extends Base implements ExclusiveDijunctable {

    private int code;
    
    @Constructor(paramsSignature = "")
    public static HashValue constructor(){
        return new HashValue(0);
    }
    
    @PlatformSpecific
    public int hashCode(){
        return code;
    }
    
    @Override
    public HashValue hashValue() {
        return this;
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
    
}
