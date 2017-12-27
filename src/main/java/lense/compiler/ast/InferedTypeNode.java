package lense.compiler.ast;

import java.util.function.Supplier;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;

public class InferedTypeNode extends TypeNode {

	private Supplier<TypedNode> originalTypeCalculator;

	public InferedTypeNode(Supplier<TypedNode> originalTypeCalculator) {
		super(false);
		this.originalTypeCalculator = originalTypeCalculator;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeVariable getTypeVariable() {
		return this.originalTypeCalculator.get().getTypeVariable();
	}
	
	/**
	 * @return
	 */
	public int getTypeParametersCount() {
		return this.originalTypeCalculator.get().getTypeVariable().getGenericParameters().size();
	}
	
	public TypeVariable getTypeParameter() {
		throw new UnsupportedOperationException("Cannot add ParamettricTypes to infered type node");
	}
	
	/**
	 * @param generic
	 */
	public void addParametricType(GenericTypeParameterNode generic) {
		throw new UnsupportedOperationException("Cannot add ParamettricTypes to infered type node");
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.originalTypeCalculator.get().getTypeVariable() == null 
				? null 
				: this.originalTypeCalculator.get().getTypeVariable().getSymbol()
				.orElse(this.originalTypeCalculator.get().getTypeVariable().getTypeDefinition().getName());
	}

	
	public void setTypeVariable(TypeVariable type){
		throw new UnsupportedOperationException("Cannot set TypeVariable on infered type node");
	}
	public void setTypeVariable(TypeDefinition type){
		throw new UnsupportedOperationException("Cannot set TypeVariable on infered type node");
	}
	

	/**
	 * @param qualifiedNameNode
	 */
	public void setName(QualifiedNameNode qualifiedNameNode) {
		throw new UnsupportedOperationException("Cannot set name to infered type node");
	}


	
	public void setTypeParameter(TypeVariable typeParameter) {
		throw new UnsupportedOperationException("Cannot set TypeParameter to infered type node");
	}

}
