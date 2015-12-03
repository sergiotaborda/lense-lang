/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class FormalParameterNode extends JavaAstNode {

	
	private TypeNode type;
	private String name;
	private ImutabilityNode imutability;
	
	public TypeDefinition getTypeDefinition() {
		return type.getTypeDefinition();
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
}
