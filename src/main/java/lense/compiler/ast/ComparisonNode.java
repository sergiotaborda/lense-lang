/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 */
public class ComparisonNode extends ExpressionNode {

	public enum Operation {
		LessThan ("<"),
		GreaterThan (">"),
		LessOrEqualTo ("<="),
		GreaterOrEqualTo(">="), 
		EqualTo ("=="), 
		Different ("!="),
		ReferenceEquals ("==="),
		ReferenceDifferent ("!=="), 
		InstanceOf("is"),
		Compare("<=>");
	    
		private String symbol;

		Operation(String symbol){
			this.symbol = symbol;
		}

		/**
		 * @return
		 */
		public String symbol() {
			return symbol;
		}

        public boolean dependsOnComparable() {
           switch (this){
               case LessThan:
               case GreaterThan:
               case LessOrEqualTo:
               case GreaterOrEqualTo:
               case Compare:
                   return true;
               default :
                  return false;
           }
        }
	}
	
	private Operation operation;

	public TypeVariable getTypeVariable() {
        return  this.operation == Operation.Compare 
                ? new FixedTypeVariable(LenseTypeSystem.Comparison())
                : new FixedTypeVariable(LenseTypeSystem.Boolean());
    }
	
	/**
	 * Constructor.
	 * @param resolveComparisonOperation
	 */
	public ComparisonNode(Operation operation) {
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}
	

	public ExpressionNode getLeft(){
		return (ExpressionNode) this.getChildren().get(0);
	}
	
	public ExpressionNode getRight(){
		return (ExpressionNode) this.getChildren().get(1);
	}
}
