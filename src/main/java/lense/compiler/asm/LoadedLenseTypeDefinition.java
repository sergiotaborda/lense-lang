package lense.compiler.asm;

import java.util.Collections;
import java.util.List;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.typesystem.FundamentalLenseTypeDefinition;

public class LoadedLenseTypeDefinition extends FundamentalLenseTypeDefinition {


	public LoadedLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
		super(name, kind, superDefinition);
	}

	public LoadedLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition,IntervalTypeVariable... parameters) {
		super(name, kind, superDefinition,parameters);
	}


	public void setGenericParameters(List<IntervalTypeVariable> typeVar) {

		if (!genericParameters.isEmpty()) {
			throw new IllegalStateException("Generics already been set");
		}
		
		for(IntervalTypeVariable variable : typeVar){
			String genericParameterSymbol = variable.getSymbol().orElseThrow(() -> new RuntimeException("Generic parameter symbol is necessary"));
			if (!genericParametersMapping.containsKey(genericParameterSymbol)){
				genericParameters.add(variable);
				genericParametersMapping.put(genericParameterSymbol, genericParameters.size() - 1);
				
				
			}
		}

		genericParameters = Collections.unmodifiableList(genericParameters);
	}
}
