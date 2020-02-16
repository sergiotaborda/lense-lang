package lense.compiler.type.variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.context.SemanticContext;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

public class LazyTypeVariable implements TypeVariable {

	private String name;
	private TypeVariable realTypeVariable = null;
	private SemanticContext semanticContext;
	private int genericsParameterCount;

	public LazyTypeVariable(String name, int genericsParameterCount, SemanticContext semanticContext) {
		this.name = name;
		this.genericsParameterCount = genericsParameterCount;
		this.semanticContext = semanticContext;
	}
	
	private Optional<TypeVariable> realTypeVariable () {
		if (realTypeVariable == null) {
			var opt = semanticContext.resolveTypeForName(name, genericsParameterCount);
			
			if (opt.isPresent()) {
				realTypeVariable = opt.get();
			} else {
				return opt;
			}
		}
		
		return Optional.of(realTypeVariable);
	}
	
	@Override
	public TypeVariable getLowerBound() {
		return realTypeVariable().orElseThrow().getLowerBound();
	}

	@Override
	public TypeVariable getUpperBound() {
		return realTypeVariable().orElseThrow().getUpperBound();
	}

	@Override
	public Variance getVariance() {
		return realTypeVariable().orElseThrow().getVariance();
	}

	@Override
	public Optional<String> getSymbol() {
		return Optional.of(name);
	}

	@Override
	public List<TypeVariable> getGenericParameters() {
		if (this.genericsParameterCount == 0) {
			return Collections.emptyList();
		}
		
		return realTypeVariable().map(t -> t.getGenericParameters())
				.orElseGet(() -> new ArrayList<TypeVariable>(this.genericsParameterCount) );
	}

	@Override
	public TypeDefinition getTypeDefinition() {
		return realTypeVariable().orElseThrow().getTypeDefinition();
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
	   if (realTypeVariable != null) {
		   return realTypeVariable;
	   }
	   return this;
	}

	@Override
	public boolean isSingleType() {
		return realTypeVariable().orElseThrow().isSingleType();
	}

	@Override
	public boolean isFixed() {
		return realTypeVariable().orElseThrow().isFixed();
	}

	@Override
	public boolean isCalculated() {
		return false;
	}

	@Override
	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
		// no-op
	}

	@Override
	public Optional<String> getTypeName() {
		return Optional.of(name);
	}

}
