package lense.compiler.ast;

import lense.compiler.phases.ReificationVisitor;

public class ReceiveReifiedTypesNodes extends FormalParameterNode  {

	private static final ReceiveReifiedTypesNodes ME = new ReceiveReifiedTypesNodes();
	
	public static ReceiveReifiedTypesNodes getInstance() {
		return ME;
	}
	
	private ReceiveReifiedTypesNodes() {
		super(ReificationVisitor.TYPE_REIFICATION_INFO);

		setTypeNode(new TypeNode("lense.core.lang.reflection.ReifiedArguments"));
	}
}
