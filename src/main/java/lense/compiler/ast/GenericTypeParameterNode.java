/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Variance;


/**
 * 
 */
public class GenericTypeParameterNode extends LenseAstNode implements TypedNode {

	
	private TypeNode typeNode;
	private Variance variance;

	
	public GenericTypeParameterNode (){}
	/**
	 * Constructor.
	 * @param typeNode2
	 * @param variance2
	 */
	public GenericTypeParameterNode(TypeNode typeNode) {
		this(typeNode , Variance.Invariant);
	}

	/**
	 * Constructor.
	 * @param typeNode2
	 * @param variance2
	 */
	public GenericTypeParameterNode(TypeNode typeNode, Variance variance) {
		this.typeNode = typeNode;
		if (typeNode != null){
			this.add(typeNode);
		}
		
		this.variance = variance;
	}

	public TypeNode getTypeNode() {
		return typeNode;
	}

	public void setTypeNode(TypeNode typeNode) {
		this.typeNode = typeNode;
		this.add(typeNode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeVariable getTypeVariable() {
		return typeNode.getTypeVariable();
	}
	
	@Override
	public void setTypeVariable(TypeVariable typeVariable) {
		typeNode.setTypeVariable(typeVariable);
	}

	public Variance getVariance() {
		return variance;
	}

	public void setVariance(Variance variance) {
		this.variance = variance;
	}
}
