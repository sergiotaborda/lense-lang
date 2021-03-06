package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Signature;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;

@Signature(":lense.core.lang.Maybe<lense.core.lang.Nothing>:")
public class None extends Maybe {

	public static final None NONE = new None();
	public static final TypeResolver TYPE_RESOLVER = TypeResolver.of(new Type(None.class).withGenerics(Nothing.TYPE_RESOLVER.resolveType()));

    @Constructor(paramsSignature = "")
	public static None constructor(){
		return NONE;
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof None;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(0);
	}

	@Override
	public String asString() {
		return String.valueOfNative("none");
	}

	@Override
	public boolean isPresent() {
		return false;
	}

	@Override
	public boolean isAbsent() {
		return true;
	}

    @Override
    public Maybe map(Function transformer) {
        return this;
    }

	
    @Override
	@lense.core.lang.java.Property( name = "value")
	@MethodSignature( returnSignature = "T" , paramsSignature = "", declaringType = "lense.core.lang.Maybe")
	public Any getValue() {
		throw new IllegalIndexException();
	}

	@Override
	public boolean valueEqualsTo(Any content) {
		return false;
	}

    @Override
    public Type type() {
        return TYPE_RESOLVER.resolveType();
    }
}
