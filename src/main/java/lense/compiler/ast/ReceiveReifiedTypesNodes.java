package lense.compiler.ast;

import lense.compiler.phases.ReificationVisitor;

public class ReceiveReifiedTypesNodes extends FormalParameterNode  {

	
	public ReceiveReifiedTypesNodes() {
		super(ReificationVisitor.REIFICATION_INFO);

		setTypeNode(new TypeNode("lense.core.lang.reflection.ReifiedArguments"));
	}
}
