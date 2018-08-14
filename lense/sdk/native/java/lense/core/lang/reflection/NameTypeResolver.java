package lense.core.lang.reflection;

public final class NameTypeResolver extends TypeResolver {

	// TODO better concurrency usage

	private Type type;
	private final String name;
	private final TypeResolver[] resolvers;
	
	public NameTypeResolver(String name) {
		this.name = name;
		this.resolvers = new TypeResolver[0];
	}
	
	public NameTypeResolver(String name, TypeResolver ... resolvers ) {
		this.name = name;	
		this.resolvers = resolvers;
	}
	
	@Override
	public TypeResolver withGenerics(TypeResolver... resolvers) {
		return new NameTypeResolver(name, resolvers);
	}
	
	@Override
	public Type resolveType() {
		if (type == null) {
			type = Type.fromName(name);
			
			if (resolvers.length > 0) {
				Type[] types = new Type[resolvers.length];
				
				for (int i =0; i < types.length; i++) {
					types[i] = resolvers[i].resolveType();
				}
				
				type = type.withGenerics(types);
			}
		}
		return type;
	}

}
