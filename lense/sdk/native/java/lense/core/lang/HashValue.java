package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public final class HashValue extends Base implements ExclusiveDijunctable {

    private int code;
    
    @Constructor(paramsSignature = "")
    public static HashValue constructor(){
        return new HashValue(0);
    }
    
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

    public HashValue concat(HashValue other){
        return new HashValue(code + 31 * other.code);
    }


    @Override
    public HashValue xor(ExclusiveDijunctable other) {
        if (other instanceof HashValue){
            return new HashValue(this.code ^ ((HashValue)other).code);
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }
    
}
