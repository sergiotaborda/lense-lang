/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class TypeNode extends JavaAstNode implements TypedNode{

	private boolean isVoid;
	private QualifiedNameNode name;
	private TypeDefinition type;

	public TypeNode() {
		this(false);
	}
	
	/**
	 * Constructor.
	 * @param b
	 */
	public TypeNode(boolean isVoid) {
		this.isVoid = isVoid;
	}

	/**
	 * Constructor.
	 * @param object
	 */
	public TypeNode(QualifiedNameNode name) {
		this.name = name;
	}

	/**
	 * Constructor.
	 * @param string
	 */
	public TypeNode(String name) {
		this(new QualifiedNameNode(name));
	}

	/**
	 * @param generic
	 */
	public void setParametricTypes(GenericTypeParameterNode generic) {
		this.add(generic);
	}

	/**
	 * @return
	 */
	public String getName() {
		return isVoid ? "java.Void" : name.toString();
	}

	/**
	 * @return
	 */
	public boolean isVoid() {
		return this.isVoid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition getTypeDefinition() {
		return this.type;
	}
	
	public void setTypeDefinition(TypeDefinition type){
		this.type = type;
	}

}
