package lense.compiler.ast;

public class ReceiveReifiedTypesNodes extends FormalParameterNode  {

	
	public ReceiveReifiedTypesNodes() {
		super("_reificiationInfo");

		setTypeNode(new TypeNode("lense.core.lang.reflection.ReifiedArguments"));
	}
}
