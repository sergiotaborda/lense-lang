package lense.compiler.crosscompile;

import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.TypeVariable;

public class ErasurePointNode extends ExpressionNode {

	public enum BoxingDirection {
		BOXING_IN, // wrap primitive
		BOXING_OUT, // convert to primitive
		NONE // boxing does not apply
	}
	
	public enum ErasureOperation {
	   CONVERSION, // converts the node to another type (cast, constructor, or primitive erasure)
	   CONVERSION_TO_PRIMITIVE, // force conversion to primitive type
	   BOXING, // puts or gets a node in a container like maybe or array, or 
	}
	
	public static ErasurePointNode box(ExpressionNode expression, TypeVariable expectedType){
	    if (expectedType == null){
	        throw new IllegalArgumentException("Type is required");
	    }
	    return new ErasurePointNode(expression, expectedType, ErasureOperation.BOXING, BoxingDirection.BOXING_IN);
	}
	
    public static ErasurePointNode unbox(ExpressionNode expression, TypeVariable expectedType){
        if (expectedType == null){
            throw new IllegalArgumentException("Type is required");
        }
        return new ErasurePointNode(expression, expectedType, ErasureOperation.BOXING, BoxingDirection.BOXING_OUT);
    }
    
    public static ErasurePointNode convertTo(ExpressionNode expression, TypeVariable expectedType){
        if (expectedType == null){
            throw new IllegalArgumentException("Type is required");
        }
        return new ErasurePointNode(expression, expectedType, ErasureOperation.CONVERSION, BoxingDirection.NONE);
    }
    
    public static ErasurePointNode convertToPrimitive(ExpressionNode expression){
        return new ErasurePointNode(expression, expression.getTypeVariable(), ErasureOperation.CONVERSION_TO_PRIMITIVE, BoxingDirection.NONE);
    }
    
	private BoxingDirection boxingDirection;
	private boolean canElide = true; // can be removed if the value is already of the expected type 
    private ErasureOperation erasureOperation;
	

	private ErasurePointNode(ExpressionNode expression, TypeVariable expectedType, ErasureOperation erasureOperation,  BoxingDirection boxingDirection){
		this.add(expression);
		super.setTypeVariable(expectedType);
		this.erasureOperation = erasureOperation;
		this.boxingDirection = boxingDirection;
	}


	public ExpressionNode getValue() {
		return (ExpressionNode)this.getChildren().get(0);
	}

	public boolean isBoxingDirectionOut() {
		return boxingDirection == BoxingDirection.BOXING_OUT;
	}

	public boolean canElide() {
		return canElide;
	}

	public void setCanElide(boolean canElide) {
		this.canElide = canElide;
	}

	
	public String toString() {
		return   erasureOperation.name() 
		        + "[" 
		        +  this.getFirstChild().toString() 
		        + " "
		        +  (erasureOperation == ErasureOperation.BOXING ?  boxingDirection.name() : " TO " ) 
		        +  " " 
		        + getTypeVariable().toString() 
		        + "]";
	}

    public ErasureOperation getErasureOperation() {
        return erasureOperation;
    }
    
    public BoxingDirection getBoxingDirection() {
        return boxingDirection;
    }

	
    public ErasurePointNode simplify() {
		if (this.getValue() instanceof ErasurePointNode) {
			ErasurePointNode child = (ErasurePointNode)this.getValue();
			
			if (child.boxingDirection == this.boxingDirection
					&& child.erasureOperation == this.erasureOperation
			) {
				return child.simplify();
			}
		}
		return this;
	}

	
    public ExpressionNode elideAll() {
    	if (this.getValue() instanceof ErasurePointNode) {
    		return ((ErasurePointNode)this.getValue()).elideAll();
		}
		return this.getValue();
	}

}
