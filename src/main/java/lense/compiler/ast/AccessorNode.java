package lense.compiler.ast;

public class AccessorNode extends InvocableDeclarionNode {

	private boolean implicit;
	private BlockNode statements;
	
	public AccessorNode(boolean implicit) {
		this.implicit= implicit;
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
	
}
