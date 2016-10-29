/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class TypeNode extends LenseAstNode implements TypedNode{


	private QualifiedNameNode name;
	private TypeVariable type;
	private IntervalTypeVariable typeParameter;
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
	/**
	 * Constructor.
	 * @param object
	 */
	public TypeNode(TypeDefinition  type) {
		this.name = new QualifiedNameNode(type.getName());
		this.setTypeVariable(new FixedTypeVariable(type));
		
		for(IntervalTypeVariable p : type.getGenericParameters()){
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
	public void setTypeVariable(TypeDefinition type){
		this.type = new FixedTypeVariable(type);
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

	public IntervalTypeVariable getTypeParameter() {
		return this.typeParameter;
	}
	
	public void setTypeParameter(IntervalTypeVariable typeParameter) {
		this.typeParameter = typeParameter;
	}


}
