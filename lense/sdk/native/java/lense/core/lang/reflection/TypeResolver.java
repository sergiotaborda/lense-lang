package lense.core.lang.reflection;

import java.util.function.Supplier;

import lense.core.lang.java.PlatformSpecific;


public abstract class TypeResolver {

	
	public abstract lense.core.lang.reflection.Type resolveType();

	public abstract TypeResolver withGenerics(TypeResolver ... resolvers);
	
	
	@PlatformSpecific
	public static TypeResolver lazy(Supplier<Type> supplier) {
		return new LazyTypeResolver(supplier);
	}
	
	@PlatformSpecific
	public static TypeResolver of(Type type) {
		return new ConstantTypeResolver(type);
	}
	
	@PlatformSpecific
	public static TypeResolver byName(String name) {
		return new NameTypeResolver(name);
	}
	
	@PlatformSpecific
	public static TypeResolver byGenericParameter(TypeResolver original, int parameterIndex) {
		return lazy(() -> {
			return original.resolveType().getGenericTypeAt(parameterIndex);
		});
	}
}
