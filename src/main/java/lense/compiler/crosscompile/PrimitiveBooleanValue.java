package lense.compiler.crosscompile;

import lense.compiler.ast.BooleanValue;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class PrimitiveBooleanValue extends BooleanValue {

	public PrimitiveBooleanValue(boolean value) {
		this.setValue(value);
	}
	
	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(new PrimitiveTypeDefinition("boolean"));
	}

}