package lense.compiler.crosscompile.java;

public enum JavaTypeKind implements lense.compiler.type.TypeKind{
	Primitive;

	@Override
	public boolean isInterface() {
		return false;
	}


}
