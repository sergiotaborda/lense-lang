/**
 * 
 */
package lense.compiler.ast;

import compiler.typesystem.GenericTypeParameter;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class TypeNode extends LenseAstNode implements TypedNode{

	private boolean isVoid;
	private QualifiedNameNode name;
	private TypeDefinition type;

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
	public TypeNode(TypeDefinition type) {
		this.name = new QualifiedNameNode(type.getName());
		this.setTypeDefinition(type);
		
		for(GenericTypeParameter p : type.getGenericParameters()){
			this.add(new GenericTypeParameterNode(new TypeNode(p.getLowerBound()), p.getVariance()));
		}
	}


	/**
	 * Constructor.
	 * @param object
	 */
	public TypeNode(QualifiedNameNode name) {
		this.name = name;
	}

	/**
	 * @param generic
	 */
	public void addParametricType(GenericTypeParameterNode generic) {
		this.add(generic);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name.toString();
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
	/**
	 * @return
	 */
	public int getGenericParametersCount() {
		if (this.getChildren() == null || this.getChildren().isEmpty()){
			return 0;
		} else {
			return this.getChildren().size();
		}
	}
	/**
	 * @param qualifiedNameNode
	 */
	public void setName(QualifiedNameNode qualifiedNameNode) {
		this.name = qualifiedNameNode;
	}


}
