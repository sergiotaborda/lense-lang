/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.Imutability;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.TypeNode;
import compiler.syntax.AstNode;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.VariableInfo;

/**
 * 
 */
public class VariableDeclarationNode extends LenseAstNode implements ScopedVariableDefinitionNode{

	
	private TypeNode type;
	private String name;
	private ExpressionNode inicializer;
	private VariableInfo info;
	private ImutabilityNode imutability;
	
	public TypeDefinition getTypeDefinition() {
		return type.getTypeDefinition();
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
			exp.setTypeDefinition(this.inicializer.getTypeDefinition());
			this.inicializer = exp;
		}
	}
}
