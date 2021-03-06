/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.context.VariableInfo;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;

/**
 * 
 */
public class FieldDeclarationNode extends AnnotadedLenseAstNode implements ScopedVariableDefinitionNode{

	private TypeNode typeNode;
	private String name;
	private ExpressionNode inicializer;
	private VariableInfo info;
	private Imutability imutability;
	private VisibilityNode visibilityNode;
	private boolean initializedOnConstructor;

	public FieldDeclarationNode (){}
	
	public FieldDeclarationNode (String name, TypeNode typeNode){
		this.name = name;
		this.imutability = Imutability.Mutable;
		this.typeNode = typeNode;
	}
	
	public FieldDeclarationNode (String name, TypeNode typeNode, ExpressionNode inicializer){
		this.name = name;
		this.imutability = Imutability.Mutable;
		this.typeNode = typeNode;
		this.inicializer = inicializer;
	}
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
	
	@Override
	public void setTypeVariable(TypeVariable typeVariable) {
		typeNode.setTypeVariable(typeVariable);
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
	public void setImutability(Imutability imutability) {
		this.imutability = imutability;
	}
	
	
	public Imutability getImutability() {
		return this.imutability == null ? Imutability.Imutable : imutability;
	}

	public void setVisibility(VisibilityNode visibilityNode) {
		this.visibilityNode = visibilityNode;
	}

	public VisibilityNode getVisibility() {
		return visibilityNode;
	}

	public void setInitializedOnConstructor(boolean initializedOnConstructor) {
		this.initializedOnConstructor = initializedOnConstructor;
	}
	
	public boolean getInitializedOnConstructor() {
		return this.initializedOnConstructor;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof FieldDeclarationNode && equals((FieldDeclarationNode)other);
	}
	
	public boolean equals(FieldDeclarationNode other) {
		return this.name.equals(other.name); 
	}
	
	public int hashCode() {
		return this.name.hashCode();
	}
}
