/**
 * 
 */
package lense.compiler.ast;

import java.util.Optional;

import lense.compiler.Visibility;
import lense.compiler.ast.AnnotadedLenseAstNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ir.stack.StackInstructionList;
import lense.compiler.ir.tac.TacInstructionList;



/**
 * 
 */
public class ConstructorDeclarationNode extends InvocableDeclarionNode {

	private TypeNode returnType;
	private String name;
	private ParametersListNode parameters = new ParametersListNode();
	private BlockNode block;
	private boolean isPrimary;
	private boolean isImplicit;
	private Visibility visibility;
	
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


	public String getScopeIdentifer() {
		if (name == null){
			return "<CONSTRUCTOR>" + this.getParameters().getChildren().size();
		} else {
			return name + this.getParameters().getChildren().size();
		}
	}
	
	
}
