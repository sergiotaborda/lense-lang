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
	private Imutability imutability;
	private Visibility visibility = Visibility.Undefined;
	private boolean methodTypeBound;
	
	/**
	 * Constructor.
	 * @param name
	 */
	public FormalParameterNode(String name, TypeVariable type) {
		this.name = name;
		this.type = new TypeNode(type);
	}
	
	public FormalParameterNode(String name) {
		this.name = name;
	}
	
	public FormalParameterNode() {
	
	}

	public TypeVariable getTypeVariable() {
		if (type == null){
			return null;
		}
		if ( type.getTypeVariable() == null){
			return type.getTypeParameter();
		} else {
			return  type.getTypeVariable();
		}
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
	public void setImutability(Imutability imutability) {
		this.imutability = imutability;
	}
	
	public Imutability getImutability() {
		return this.imutability == null ? Imutability.Imutable : imutability;
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	
	public void setMethodTypeBound(boolean methodTypeBound) {
		this.methodTypeBound = methodTypeBound;
	}
	
	public boolean isMethodTypeBound( ) {
		return this.methodTypeBound ;
	}
}
