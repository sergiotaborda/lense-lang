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

public class Type extends Base {

    public static Type fromName (String name){
    	try {
			return new Type(Class.forName(name));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
    }
    
    private final Class javaType;
	private Type[] generics;

    @Constructor(paramsSignature = "")
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
        // TODO reification of Property
        return Array.fromAnyArray(null,methods.stream().toArray(lense.core.lang.reflection.Method[]::new));

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
        
        // TODO reification of Property
        return Array.fromAnyArray(null, properties.stream().toArray(Property[]::new));
    }


    public Type withGenerics(Type ... types) {
		
    	Type t = new Type(this.javaType);
    	t.generics = types;
    	
    	return t;
	}

	
    public Type getGenericTypeAt(int parameterIndex) {
    	if (generics == null) {
    		throw new ClassCastException("Type " + this.javaType.getName() + " has no generic parameters");
    	}
    	
    	if (parameterIndex > generics.length - 1) {
    		throw new ClassCastException("Type " + this.javaType.getName() + " has no generic parameters at index " + parameterIndex );
    	}
		return generics[parameterIndex];
	}
    

}
