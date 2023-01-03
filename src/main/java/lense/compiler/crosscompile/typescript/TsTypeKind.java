package lense.compiler.crosscompile.typescript;

public enum TsTypeKind implements lense.compiler.type.TypeKind{
	Primitive;

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public boolean isEnhancement() {
		return false;
	}

	@Override
	public boolean isValue() {
		return false;
	}

	@Override
	public boolean isTypeClass() {
		return false;
	}


}
