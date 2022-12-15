package lense.core.lang.reflection;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public final class ReflectiveMethod  extends Base implements Method {

	@Constructor(paramsSignature = "lense.core.lang.reflection.Type, lense.core.lang.String")
    public static ReflectiveMethod constructor(Type parent, lense.core.lang.String name ){
    	return new ReflectiveMethod(parent, name);
    }
    
	private final Type parent;
	private final lense.core.lang.String name;

	public ReflectiveMethod( Type parent, lense.core.lang.String name) {
		this.parent = parent;
		this.name = name;
	}
	
    @lense.core.lang.java.Property(name="name")
    public lense.core.lang.String  getName() {
        return name;
    }
}
