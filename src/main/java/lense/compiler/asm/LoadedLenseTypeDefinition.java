package lense.compiler.asm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.FundamentalLenseTypeDefinition;
import lense.compiler.typesystem.Visibility;

public class LoadedLenseTypeDefinition extends FundamentalLenseTypeDefinition {


	private boolean loaded = false;
	
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public LoadedLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
		super(name, kind, superDefinition);
	}

//	public LoadedLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition,TypeVariable... parameters) {
//		super(name, kind, superDefinition,parameters);
//	}


	private LoadedLenseTypeDefinition() {
		super();
	}

	public LoadedLenseTypeDefinition(LenseTypeDefinition other) {
		super();
		other.copyTo(this);
	}

	protected LenseTypeDefinition duplicate() {
		return copyTo(new LoadedLenseTypeDefinition());
	}

	public void setGenericParameters(List<TypeVariable> typeVar) {

		if (!genericParameters.isEmpty()) {
			throw new IllegalStateException("Generics already been set");
		}

		for(TypeVariable variable : typeVar){
			genericParameters.add(variable);

			variable.getSymbol().ifPresent(  symbol -> {
				genericParametersMapping.put(symbol, genericParameters.size() - 1);
			});

		}

		genericParameters = Collections.unmodifiableList(genericParameters);
	}
	
	public void forceSetGenericParameters(List<TypeVariable> typeVar) {

		genericParameters = new LinkedList<>();
		
		for(TypeVariable variable : typeVar){
			genericParameters.add(variable);

			variable.getSymbol().ifPresent(  symbol -> {
				genericParametersMapping.put(symbol, genericParameters.size() - 1);
			});

		}

		genericParameters = Collections.unmodifiableList(genericParameters);
	}
	
}
