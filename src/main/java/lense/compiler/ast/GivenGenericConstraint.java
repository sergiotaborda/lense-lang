package lense.compiler.ast;

public class GivenGenericConstraint extends LenseAstNode {

	private final String name;

	public GivenGenericConstraint(String name, TypeNode typeNode) {
		this.add(typeNode);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public TypeNode getTypeNode(){
		return (TypeNode) this.getChildren().get(0);
	}
}
