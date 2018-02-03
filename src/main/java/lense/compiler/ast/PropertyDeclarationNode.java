package lense.compiler.ast;

import lense.compiler.phases.ScopeDelimiter;

public class PropertyDeclarationNode extends InvocableDeclarionNode implements ScopeDelimiter{

	private AccessorNode acessor;
	private ModifierNode modifier;
	private String name;
	private TypeNode type;
	private AnnotationListNode annotationListNode;
	private ExpressionNode initializer;
	private boolean inicializedOnConstructor;
	
	public PropertyDeclarationNode(){}
	
	public PropertyDeclarationNode(String name, TypeNode type){
		this.name = name;
		this.type = type;
	}
	
	public boolean isIndexed(){
		return false;
	}
	
	public void setAcessor(AccessorNode node) {
		acessor= node;
		this.add(node);
	}
	
	public void setModifier(ModifierNode node) {
		modifier = node;
		this.add(node);
	}

	public void setName(String name) {
		this.name= name;
	}

	public void setType(TypeNode typeNode) {
		type = typeNode;
		this.add(type);
		
	}
	
	public void setInitializer(ExpressionNode exp) {
		initializer = exp;
		this.add(exp);
	}
	
	public ExpressionNode getInitializer (){
		return initializer;
	}

	public void setAnnotations(AnnotationListNode annotationListNode) {
		this.annotationListNode = annotationListNode;
		this.add(annotationListNode);
		
	}
	
	public AnnotationListNode getAnnotations() {
		return annotationListNode;
	}
	
	public AccessorNode getAcessor() {
		return acessor;
	}

	public ModifierNode getModifier() {
		return modifier;
	}
	
	public TypeNode getType() {
		return type;
	}
	
	public String getName () {
		return name;
	}

	public void setInitializedOnConstructor(boolean inicializedOnConstructor) {
		this.inicializedOnConstructor = inicializedOnConstructor;
	}
	
	public boolean isInicializedOnConstructor(){
		return inicializedOnConstructor;
	}

	@Override
	public String getScopeName() {
		return name;
	}



}
