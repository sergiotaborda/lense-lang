package lense.compiler.ast;

import lense.compiler.phases.ScopeDelimiter;

public class ModifierNode extends InvocableDeclarionNode implements ScopeDelimiter{

	private boolean implicit;
	private boolean declared;
	private BlockNode statement;
	private String valueVariableName = "value";


	public ModifierNode(boolean implicit, boolean  declared) {
		this.implicit= implicit;
		this.declared= declared;
	}

	public void setStatements(BlockNode node) {
		this.statement = node;
		this.add(node);
	}

	
	public PropertyDeclarationNode getParent() {
		return (PropertyDeclarationNode) super.getParent();
	}
	
	public BlockNode getBlock() {
		return statement;
	}
	
	public boolean isImplicit(){
		return implicit;
	}

	public void setValueVariableName(String name) {
		this.valueVariableName = name;
	}
	
	public String getValueVariableName(){
		return valueVariableName;
	}

	@Override
	public String getScopeName() {
		return "set";
	}

	public boolean isDeclared() {
		return declared;
	}

	
}
