package lense.core.lang.reflection;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public class Property extends Base {

    private final java.lang.reflect.Method javaMethod;
    private final lense.core.lang.java.Property propertyMetadaData;

    @Constructor(paramsSignature = "")
    public static Property constructor(){
        throw new IllegalArgumentException("Methods cannot be created directly");
    }
    
    protected Property(java.lang.reflect.Method  javaMethod, lense.core.lang.java.Property propertyMetadaData){
        this.javaMethod = javaMethod;
        this.propertyMetadaData = propertyMetadaData;            
    }
    
    @lense.core.lang.java.Property(name="name")
    public lense.core.lang.String  getName() {
        return lense.core.lang.String.valueOfNative(propertyMetadaData.name());
    }
    

}
