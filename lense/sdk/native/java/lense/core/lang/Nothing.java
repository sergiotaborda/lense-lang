package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;

public final class Nothing extends Base {

	public static final TypeResolver TYPE_RESOLVER = TypeResolver.of(Type.forClass(Nothing.class));

	private Nothing() {}; 
	
    @Override
    public Type type() {
        return TYPE_RESOLVER.resolveType();
    }
}
