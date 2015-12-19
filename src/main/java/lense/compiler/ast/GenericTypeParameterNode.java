/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.Variance;


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
		this.add(typeNode);
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
	public TypeDefinition getTypeDefinition() {
		return typeNode.getTypeDefinition();
	}

	public Variance getVariance() {
		return variance;
	}

	public void setVariance(Variance variance) {
		this.variance = variance;
	}
}
