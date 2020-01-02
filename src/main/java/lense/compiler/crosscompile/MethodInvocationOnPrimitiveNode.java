package lense.compiler.crosscompile;

import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NeedTypeCalculationNode;

public class MethodInvocationOnPrimitiveNode extends NeedTypeCalculationNode {

    private PrimitiveTypeDefinition type;

    public MethodInvocationOnPrimitiveNode(PrimitiveTypeDefinition primitiveType, MethodInvocationNode m) {
        this.type = primitiveType;
        this.add(m);
    }

    public PrimitiveTypeDefinition getTargetPrimitiveType() {
        return type;
    }

    public MethodInvocationNode getMethodInvocation() {
        return (MethodInvocationNode)this.getFirstChild();
    }


}
