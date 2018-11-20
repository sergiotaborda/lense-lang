/**
 * 
 */
package lense.compiler.ast;

import java.util.Optional;

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
	    
		private final String symbol;

		Operation(String symbol){
			this.symbol = symbol;
		}

		public Optional<String> getEquivalentMethodName(){
		    if (this == Operation.EqualTo){
		        return Optional.of("equalsTo");
		    } else  if (this == Operation.Compare){
                return Optional.of("compareWith");
            } 
		    
		    return Optional.empty();
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

        public boolean isNegatable(){
            if (this == Compare || this == Operation.InstanceOf){
                return false;
            }
            return true;
        }
        public Operation negate() {
            switch (this) {
            case Different:
                return EqualTo;
            case EqualTo:
                return Different;
            case GreaterOrEqualTo:
                return LessThan;
            case GreaterThan:
                return LessOrEqualTo;
            case LessOrEqualTo:
                return GreaterThan;
            case LessThan:
                return GreaterOrEqualTo;
            case ReferenceDifferent:
                return ReferenceEquals;
            case ReferenceEquals:
                return ReferenceDifferent;
            case Compare:
            case InstanceOf:
            default:
                throw new IllegalStateException("Can not negate operation " + this);
            }
        }
	}
	
	private Operation operation;

	public TypeVariable getTypeVariable() {
        return  this.operation == Operation.Compare 
                ? LenseTypeSystem.Comparison()
                : LenseTypeSystem.Boolean();
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
