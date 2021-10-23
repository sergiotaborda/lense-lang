package lense.core.lang.reflection;

public final class ConstantTypeResolver extends TypeResolver {

	private final Type type;
	
	public ConstantTypeResolver(Type type) {
		this.type = type;
	}
	
	@Override
	public final Type resolveType() {
		return type;
	}


	@Override
	public TypeResolver withGenerics(TypeResolver... resolvers) {
		return TypeResolver.lazy(() -> {
			Type[] types = new Type[resolvers.length];
			
			for (int i =0; i< types.length; i++) {
				types[i] = resolvers[i].resolveType();
			}
			
			return type.withGenerics(types);
		} );
	}

	
	

}
