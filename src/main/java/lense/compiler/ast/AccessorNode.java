package lense.compiler.ast;

import lense.compiler.phases.ScopeDelimiter;

public class AccessorNode extends InvocableDeclarionNode implements ScopeDelimiter{

	private boolean implicit;
	private boolean declared;
	private BlockNode statements;
	
	public AccessorNode(boolean implicit, boolean declared) {
		this.implicit= implicit;
		this.declared = declared;
	}

	
	public void setStatements(BlockNode node) {
		this.statements = node;
		this.add(node);
	}

	
	public PropertyDeclarationNode getParent() {
		return (PropertyDeclarationNode) super.getParent();
	}

	public BlockNode getBlock() {
		return statements;
	}
	
	public boolean isImplicit(){
		return implicit;
	}

	public boolean isDeclared(){
		return declared;
	}

	@Override
	public String getScopeName() {
		return "get";
	}
	
}
