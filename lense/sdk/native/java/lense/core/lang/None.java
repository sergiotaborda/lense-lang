package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Signature;

@Signature(":lense.core.lang.Maybe<lense.core.lang.Nothing>:")
public class None extends Maybe {

	public static final None NONE = new None();

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
	public Any getValue() {
		throw new IllegalIndexException();
	}

	@Override
	public boolean is(Any content) {
		return false;
	}

}
