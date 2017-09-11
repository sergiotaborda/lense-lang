package lense.core.lang.reflection;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lense.core.collections.Array;
import lense.core.collections.Sequence;
import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.PlatformSpecific;

public class Type extends Base {

    
    private final Class javaType;

    @Constructor
    public static Type constructor(){
        throw new IllegalArgumentException("Methods cannot be created directly");
    }
    
    public Type(Class javaType){
        this.javaType = javaType;
    }
    
    @lense.core.lang.java.Property(name="name")
    public lense.core.lang.String  getName() {
        return lense.core.lang.String.valueOfNative(javaType.getName());
    }
    
    public boolean isInstance(Any any)  {
        return javaType.isInstance(any);
    }
    
    @lense.core.lang.java.Property(name="methods")
    public Sequence getMethods()  {
        
       
        List<lense.core.lang.reflection.Method> methods = new LinkedList<>();
                
        for (Method javaMethod :  javaType.getMethods()){
            if (!javaMethod.isAnnotationPresent(lense.core.lang.java.PlatformSpecific.class)){
                lense.core.lang.java.Property prop = javaMethod.getDeclaredAnnotation(lense.core.lang.java.Property.class);
                if (prop == null){
                    methods.add(new lense.core.lang.reflection.Method(javaMethod));
                }
            }
        }
        
        return Array.fromAnyArray(methods.stream().toArray(lense.core.lang.reflection.Method[]::new));

    }
    
    @lense.core.lang.java.Property(name="properties")
    public Sequence getProperties()  {
      
        List<Property> properties = new LinkedList<>();
        
        Set<java.lang.String> names = new HashSet<>();
        
        for (Method javaMethod :  javaType.getMethods()){
            if (!javaMethod.isAnnotationPresent(lense.core.lang.java.PlatformSpecific.class)){
                lense.core.lang.java.Property prop = javaMethod.getDeclaredAnnotation(lense.core.lang.java.Property.class);
                if (prop != null){
                    if (names.add(prop.name())){
                        properties.add(new Property(javaMethod, prop));
                    }
                }
            } 
        }
        
        return Array.fromAnyArray(properties.stream().toArray(Property[]::new));
    }
    

}
