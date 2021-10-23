/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.FundamentalLenseTypeDefinition;

/**
 * 
 */
public class TypeNode extends LenseAstNode implements TypedNode{


	private QualifiedNameNode name;
	private TypeVariable type;
	private TypeVariable typeParameter;
	private boolean needsInference = false;
	
	protected TypeNode(boolean needsInference) {
		this.needsInference = needsInference;
	}
	
	public TypeNode(TypeVariable  type) {
		this.name = new QualifiedNameNode(type.getTypeDefinition().getName());
		this.setTypeVariable(type);
	}
	
	public boolean needsInference(){
		return this.needsInference;
	}
	
	public String toString(){
	    StringBuilder builder = new StringBuilder( name.toString());
	    if (!this.getChildren().isEmpty()){
	        builder.append("<");
	        
	        for (AstNode n : this.getChildren()){
	            GenericTypeParameterNode a = (GenericTypeParameterNode)n;
	            builder.append(a.getTypeNode().toString()).append(",");
	        }
	        builder.deleteCharAt(builder.length() - 1);
	        
	        builder.append(">");
	    }
	    
	    return builder.toString();
	}
	
	/**
	 * Constructor.
	 * @param object
	 */
	public TypeNode(TypeDefinition  type) {
		this.name = new QualifiedNameNode(type.getName());
		this.setTypeVariable(type);
		
		for(TypeVariable p : type.getGenericParameters()){
			if (p.getLowerBound() == null){
				this.add(new GenericTypeParameterNode(null, p.getVariance()));
			} else {
				this.add(new GenericTypeParameterNode(new TypeNode(p.getLowerBound()), p.getVariance()));
			}
			
		}
	}

	/**
	 * Constructor.
	 * @param object
	 */
	public TypeNode(QualifiedNameNode name) {
		this.name = name;
	}
	
	public TypeNode(String name) {
		this.name = new QualifiedNameNode(name);
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
	public TypeVariable getTypeVariable() {
		return this.type;
	}
	
	public void setTypeVariable(TypeVariable type){
		this.type = type;
	}

	
	@Override
	protected AstNode prepareAttach(AstNode node){
	    if (!(node instanceof GenericTypeParameterNode)){
	        throw new IllegalArgumentException("Not a generic parameter");
	    }
	    
	    return super.prepareAttach(node);
	}

	/**
	 * @return
	 */
	public int getTypeParametersCount() {
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

	public TypeVariable getTypeParameter() {
		return this.typeParameter != null ? this.typeParameter : this.type;
	}
	
	public void setTypeParameter(TypeVariable typeParameter) {
		this.typeParameter = typeParameter;
	}


}
