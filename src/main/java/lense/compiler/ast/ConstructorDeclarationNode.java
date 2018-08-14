/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.phases.ScopeDelimiter;

/**
 * 
 */
public class ConstructorDeclarationNode extends InvocableDeclarionNode implements ScopeDelimiter{

	private TypeNode returnType;
	private String name;
	private ParametersListNode parameters = new ParametersListNode();
	private BlockNode block;
	private boolean isPrimary= false;
	private boolean isImplicit = false;
    private ConstructorExtentionNode constructorExtentionNode;
	private TypeParametersListNode typeParametersListNode;

    
    public ConstructorDeclarationNode () {}
    
    
	public TypeNode getReturnType() {
		return returnType;
	}
	public void setReturnType(TypeNode returnType) {
		this.returnType = returnType;
		this.add(returnType);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ParametersListNode getParameters() {
		return parameters;
	}
	public void setParameters(ParametersListNode parameters) {
		this.parameters = parameters;
		this.add(parameters);
	}
	public BlockNode getBlock() {
		return block;
	}
	public void setBlock(BlockNode block) {
		this.block = block;
		this.add(block);
	}
	public boolean isPrimary() {
		return isPrimary;
	}
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public boolean isImplicit() {
		return isImplicit;
	}
	public void setImplicit(boolean isImplicit) {
		this.isImplicit = isImplicit;
	}

	@Override
	public String getScopeName() {
		if (name == null){
			return "<CONSTRUCTOR>" + this.getParameters().getChildren().size();
		} else {
			return name + this.getParameters().getChildren().size();
		}
	}
    
	public void setExtention(ConstructorExtentionNode constructorExtentionNode) {
       this.constructorExtentionNode = constructorExtentionNode;
       this.add(constructorExtentionNode);
    }
	
	public ConstructorExtentionNode getExtention(){
	    return this.constructorExtentionNode;
	}

	public void setMethodScopeGenerics(TypeParametersListNode typeParametersListNode) {
		this.typeParametersListNode = typeParametersListNode;
		this.add(typeParametersListNode);
	}
	public TypeParametersListNode getMethodScopeGenerics() {
		return typeParametersListNode;
	}

	
}
