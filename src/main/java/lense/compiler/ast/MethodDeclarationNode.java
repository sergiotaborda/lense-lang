/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class MethodDeclarationNode extends InvocableDeclarionNode {

	private TypeNode returnType;
	private String name;
	private ParametersListNode parameters = new ParametersListNode();
	private BlockNode block;

	
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
	
	
}
