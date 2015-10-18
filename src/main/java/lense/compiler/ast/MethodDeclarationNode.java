/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.AnnotadedSenseAstNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.TypeNode;



/**
 * 
 */
public class MethodDeclarationNode extends AnnotadedSenseAstNode {

	private TypeNode returnType;
	private String name;
	private ParametersListNode parameters;
	private BlockNode block;
	private boolean isAbstract;
	
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
	public boolean isAbstract() {
		return isAbstract;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	
	
}
