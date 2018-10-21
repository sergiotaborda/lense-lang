package lense.compiler.ast;

import lense.compiler.crosscompile.PrimitiveTypeDefinition;
import lense.compiler.type.variable.TypeVariable;

public class CastNode extends ExpressionNode {

	
	private boolean tupleAccessMethod;

	public void setTypeVariable(TypeVariable type) {
		 if (type instanceof PrimitiveTypeDefinition) {
			 throw new IllegalArgumentException();
		 }
		 super.setTypeVariable(type);
	}
	
	public CastNode (LenseAstNode other, TypeVariable type){
	    if (type == null){
            throw new IllegalArgumentException("Type is necessary");
        }
		this.add(other);
		this.setTypeVariable(type);
		
		if (other instanceof MethodInvocationNode) {
			this.tupleAccessMethod = ((MethodInvocationNode) other).isTupleAccessMethod();
		}
	}

	public LenseAstNode getInner(){
		return (LenseAstNode) this.getChildren().get(0);
	}


	public String toString() {
		return "((" + this.getTypeVariable().toString() + ")" + getInner().toString() + ")";
	}

	
	public boolean isTupleAccessMethod() {
		return tupleAccessMethod;
	}

	public void setTupleAccessMethod(boolean tupleAccessMethod) {
		this.tupleAccessMethod = tupleAccessMethod;
	}
}

