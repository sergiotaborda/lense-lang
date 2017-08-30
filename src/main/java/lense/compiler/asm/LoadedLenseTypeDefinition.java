package lense.compiler.asm;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.typesystem.FundamentalLenseTypeDefinition;

class LoadedLenseTypeDefinition extends FundamentalLenseTypeDefinition {

	public LoadedLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
		super(name, kind, superDefinition);
	}

	public LoadedLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition,IntervalTypeVariable... parameters) {
		super(name, kind, superDefinition,parameters);
	}
}
