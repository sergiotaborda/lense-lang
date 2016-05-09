/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class VariableDeclarationNode extends LenseAstNode implements ScopedVariableDefinitionNode{

	
	private TypeNode type;
	private String name;
	private ExpressionNode inicializer;
	private VariableInfo info;
	private ImutabilityNode imutability;
	
	public TypeVariable getTypeVariable() {
		return type.getTypeVariable();
	}

	public TypeNode getTypeNode() {
		return type;
	}

	
	public void setTypeNode(TypeNode type) {
		this.type = type;
		this.add(type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/**
	 * 
	 */
	public ExpressionNode getInitializer() {
		return inicializer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitializer(ExpressionNode node) {
		this.inicializer = node;
		this.add(node);
	}

	/**
	 * @param info
	 */
	public void setInfo(VariableInfo info) {
		this.info = info;
	}
	
	public VariableInfo getInfo(){
		return info;
	}
	
	/**
	 * @param astNode
	 */
	public void setImutability(ImutabilityNode imutability) {
		this.imutability = imutability;
	}
	
	public Imutability getImutabilityValue() {
		return this.imutability == null ? Imutability.Mutable : imutability.getImutability();
	}
	
	public void replace(AstNode node, AstNode newnode){
		super.replace(node, newnode);
		
		if (this.inicializer == node){
			ExpressionNode exp = (ExpressionNode) newnode;
			exp.setTypeVariable(this.inicializer.getTypeVariable());
			this.inicializer = exp;
		}
	}
}
