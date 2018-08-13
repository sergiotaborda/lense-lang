package lense.core.lang.reflection;

import java.util.function.Supplier;

public final class LazyTypeResolver extends TypeResolver {

	// TODO better concurrency usage
	private final Supplier<Type> supplier;
	private Type type;

	public LazyTypeResolver(Supplier<Type> supplier) {
		this.supplier = supplier;	
	}

	@Override
	public Type resolveType() {
		if (type == null) {
			type = supplier.get();
		}
		return type;
	}


	@Override
	public TypeResolver withGenerics(TypeResolver... resolvers) {

		return TypeResolver.lazy(() -> {
			Type[] types = new Type[resolvers.length];

			for (int i =0; i< types.length; i++) {
				types[i] = resolvers[i].resolveType();
			}

			return supplier.get().withGenerics(types);
		} );

	}




}
