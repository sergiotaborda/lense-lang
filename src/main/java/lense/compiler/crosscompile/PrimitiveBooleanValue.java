package lense.compiler.crosscompile;

import lense.compiler.ast.BooleanValue;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class PrimitiveBooleanValue extends BooleanValue {

    private static ErasedTypeDefinition erased = new ErasedTypeDefinition(LenseTypeSystem.Boolean(),  PrimitiveTypeDefinition.BOOLEAN);
    
	public PrimitiveBooleanValue(boolean value) {
		this.setValue(value);
	}
	
	public TypeVariable getTypeVariable() {
		return erased;
	}

}
