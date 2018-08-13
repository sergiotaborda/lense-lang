package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Signature;
import lense.core.lang.reflection.ReifiedArguments;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;
import lense.core.math.Natural;

@Signature("[=T<lense.core.lang.Any]::")
public class Some extends Maybe{

	private final Any value;
	private final TypeResolver innerTypeResolver;
	
    @Constructor(paramsSignature = "T")
	public static Some constructor(ReifiedArguments args, Any value){
		return new Some(value,args.typeAt(Natural.ZERO));
	}
	
	private Some(Any value, TypeResolver innerTypeResolver){
		this.value =value;
		this.innerTypeResolver = innerTypeResolver;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Some && equalsTo((Some)other);
	}
	
	public boolean equalsTo(Some other) {
		return this.value.equalsTo(other.value);
	}

	@Override
	public HashValue hashValue() {
		return value.hashValue();
	}

	@Override
	public String asString() {
		return value.asString();
	}


	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public boolean isAbsent() {
		return false;
	}

    @Override
    public Maybe map(Function transformer) {
        return new Some(transformer.apply(this.value), null); // TODO this method is generic bound and should have $refication$m parameter
    }

	@Override
	public Any getValue() {
		return value;
	}

	@Override
	public boolean is(Any content) {
		return value.equalsTo(content);
	}
	
    @Override
    public Type type() {
        return new Type(this.getClass()).withGenerics(innerTypeResolver.resolveType());
    }
}
