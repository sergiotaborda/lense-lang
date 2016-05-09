/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.AnnotadedLenseAstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.Imutability;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class FieldDeclarationNode extends AnnotadedLenseAstNode implements ScopedVariableDefinitionNode{

	private TypeNode typeNode;
	private String name;
	private ExpressionNode inicializer;
	private VariableInfo info;
	private ImutabilityNode imutability;

	
	/**
	 * @param typeNode
	 */
	public void setTypeNode(TypeNode typeNode) {
		this.typeNode = typeNode;
		this.add(typeNode);
	}
	
	public TypeNode getTypeNode(){
		return typeNode;
	}
	
	public TypeVariable getTypeVariable() {
		return typeNode.getTypeVariable();
	}

	public String getName() {
		return name;
	}

	/**
	 * Atributes {@link String}.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Obtains {@link ExpressionNode}.
	 * @return the inicializer
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
		this.add(inicializer);
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
	
	public ImutabilityNode getImutability() {
		return this.imutability;
	}
	
	public Imutability getImutabilityValue() {
		return this.imutability == null ? Imutability.Mutable : imutability.getImutability();
	}
	
}
