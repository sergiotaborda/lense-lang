/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.phases.ScopeDelimiter;
import lense.compiler.type.Method;

/**
 * 
 */
public class MethodDeclarationNode extends InvocableDeclarionNode implements ScopeDelimiter{

	private TypeNode returnType;
	private String name;
	private ParametersListNode parameters = new ParametersListNode();
	private BlockNode block;

	private boolean isProperty;
	private String propertyName;
	private boolean isIndexer;
	private boolean isSetter;
	
	private Method method;
	private Method superMethod;
	private TypeParametersListNode typeParametersListNode;
	
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
	public boolean isProperty() {
		return isProperty;
	}
	public void setProperty(boolean isProperty) {
		this.isProperty = isProperty;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public boolean isIndexer() {
		return isIndexer;
	}
	public void setIndexer(boolean isIndexer) {
		this.isIndexer = isIndexer;
	}
	public boolean isSetter() {
		return isSetter;
	}
	public void setSetter(boolean isSetter) {
		this.isSetter = isSetter;
	}
	@Override
	public String getScopeName() {
		return name;
	}
	
	public Method getSuperMethod() {
		return superMethod;
	}
	public void setSuperMethod(Method superMethod) {
		this.superMethod = superMethod;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	
	public void setMethodScopeGenerics(TypeParametersListNode typeParametersListNode) {
		this.typeParametersListNode = typeParametersListNode;
		this.add(typeParametersListNode);
	}
	public TypeParametersListNode getMethodScopeGenerics() {
		return typeParametersListNode == null ? new TypeParametersListNode():  typeParametersListNode;
	}


	

	

	
	
}
