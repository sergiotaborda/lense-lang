/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class FormalParameterNode extends LenseAstNode implements TypedNode {

	
	private TypeNode type;
	private String name;
	private ImutabilityNode imutability;
	private Visibility visibility = Visibility.Undefined;
	
	/**
	 * Constructor.
	 * @param name
	 */
	public FormalParameterNode(String name) {
		this.name = name;
	}
	public FormalParameterNode() {
	
	}

	public TypeVariable getTypeVariable() {
		return type == null ? null : type.getTypeVariable();
	}
	
	@Override
	public void setTypeVariable(TypeVariable typeVariable) {
		type.setTypeVariable(typeVariable);
	}

	public TypeNode getTypeNode() {
		return type;
	}

	
	public void setTypeNode(TypeNode type) {
		this.type = type;
		this.add(type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @param astNode
	 */
	public void setImutability(ImutabilityNode imutability) {
		this.imutability = imutability;
	}
	
	public Imutability getImutabilityValue() {
		return this.imutability == null ? Imutability.Mutable : imutability.getImutability();
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	
}
