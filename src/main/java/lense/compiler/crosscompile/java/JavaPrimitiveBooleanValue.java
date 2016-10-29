package lense.compiler.crosscompile.java;

import lense.compiler.ast.BooleanValue;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class JavaPrimitiveBooleanValue extends BooleanValue {

	public JavaPrimitiveBooleanValue(boolean value) {
		this.setValue(value);
	}
	
	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(new JavaPrimitiveTypeDefinition("boolean"));
	}

}
