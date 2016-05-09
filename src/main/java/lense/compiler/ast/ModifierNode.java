package lense.compiler.ast;

public class ModifierNode extends InvocableDeclarionNode {

	private boolean implicit;
	private BlockNode statement;


	public ModifierNode(boolean implicit) {
		this.implicit= implicit;
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
}
