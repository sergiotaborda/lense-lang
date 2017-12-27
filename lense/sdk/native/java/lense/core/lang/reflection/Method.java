package lense.core.lang.reflection;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public class Method extends Base {

    private java.lang.reflect.Method javaMethod;

    @Constructor(paramsSignature = "")
    public static Method constructor(){
        throw new IllegalArgumentException("Methods cannot be created directly");
    }
    
    protected Method(java.lang.reflect.Method  javaMethod){
        this.javaMethod = javaMethod;
    }
    
    @lense.core.lang.java.Property(name="name")
    public lense.core.lang.String  getName() {
        return lense.core.lang.String.valueOfNative(javaMethod.getName());
    }
    

}
