package lense.core.lang.reflection;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lense.core.collections.Array;
import lense.core.collections.Sequence;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.JavaReflectionMethod;
import lense.core.lang.java.JavaReflectionProperty;
import lense.core.lang.java.NativeString;

public final class NativeType  extends Type {

    @Constructor(paramsSignature = "")
    public static Type constructor(){
        throw new IllegalArgumentException("Methods cannot be created directly");
    }
    
    public NativeType(Class<?> javaType){
        this.instanceReflectionClass = javaType;
    }
    
	protected Class<?> instanceClass(){
		return this.instanceReflectionClass;
	}
	
    @lense.core.lang.java.Property(name="name")
    public lense.core.lang.String  getName() {
        return NativeString.valueOfNative(instanceReflectionClass.getName());
    }
    
    @lense.core.lang.java.Property(name="methods")
    public Sequence loadMethods()  {
        
       
        List<lense.core.lang.reflection.Method> methods = new LinkedList<>();
                
        for (Method javaMethod :  instanceReflectionClass.getMethods()){
            if (!javaMethod.isAnnotationPresent(lense.core.lang.java.PlatformSpecific.class)){
                lense.core.lang.java.Property prop = javaMethod.getDeclaredAnnotation(lense.core.lang.java.Property.class);
                if (prop == null){
                    methods.add(new JavaReflectionMethod(javaMethod));
                }
            }
        }
        // TODO reification of Property
        return Array.fromAnyArray(null,methods.stream().toArray(lense.core.lang.reflection.Method[]::new));

    }
    
    @lense.core.lang.java.Property(name="properties")
    public Sequence loadProperties()  {
      
        List<Property> properties = new LinkedList<>();
        
        Set<java.lang.String> names = new HashSet<>();
        
        for (Method javaMethod :  instanceReflectionClass.getMethods()){
            if (!javaMethod.isAnnotationPresent(lense.core.lang.java.PlatformSpecific.class)){
                lense.core.lang.java.Property prop = javaMethod.getDeclaredAnnotation(lense.core.lang.java.Property.class);
                if (prop != null){
                    if (names.add(prop.name())){
                        properties.add(new JavaReflectionProperty(javaMethod, prop));
                    }
                }
            } 
        }
        
        // TODO reification of Property
        return Array.fromAnyArray(null, properties.stream().toArray(Property[]::new));
    }

	@Override
	public Type duplicate() {
	 	return new NativeType(this.instanceReflectionClass);
	}
    
}
