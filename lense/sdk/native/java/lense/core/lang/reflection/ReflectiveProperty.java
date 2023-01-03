package lense.core.lang.reflection;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public final class ReflectiveProperty  extends Base implements Property{

	@Constructor(paramsSignature = "lense.core.lang.reflection.Type, lense.core.lang.String")
    public static ReflectiveProperty constructor(Type parent, lense.core.lang.String name ){
    	return new ReflectiveProperty(parent, name);
    }
    
	private final Type parent;
	private final lense.core.lang.String name;

	public ReflectiveProperty( Type parent, lense.core.lang.String name) {
		this.parent = parent;
		this.name = name;
	}
	
    @lense.core.lang.java.Property(name="name")
    public lense.core.lang.String  getName() {
        return name;
    }
}
