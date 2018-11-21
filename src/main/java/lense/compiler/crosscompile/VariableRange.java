package lense.compiler.crosscompile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import compiler.syntax.AstNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.RangeNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.type.TypeDefinition;

public class VariableRange {

    public static VariableRange EMPTY = new VariableRange(null,null, false);
    private static VariableRange INT_RANGE = new VariableRange(BigDecimal.valueOf(Integer.MIN_VALUE),BigDecimal.valueOf(Integer.MAX_VALUE), true);
    private static VariableRange Long_RANGE = new VariableRange(BigDecimal.valueOf(Long.MIN_VALUE),BigDecimal.valueOf(Long.MAX_VALUE), true);

    public static VariableRange forType(TypeDefinition type ){

        if (type == PrimitiveTypeDefinition.INT){
            return INT_RANGE;
        } else  if (type == PrimitiveTypeDefinition.LONG){
            return Long_RANGE;
        }

        return EMPTY;
    }

    public static VariableRange extractFrom(AstNode node){

        if (node instanceof NumericValue){
            NumericValue n = (NumericValue)node;
            
            return new VariableRange (
                    (BigDecimal) n.getValue(),
                    (BigDecimal) n.getValue(),
                    true);
        } else if (node instanceof MethodInvocationNode){
            MethodInvocationNode m = (MethodInvocationNode)node;

            return new VariableRange (
                    m.getProperty("minimum", BigDecimal.class).orElse(null),
                    m.getProperty("maximum", BigDecimal.class).orElse(null),
                    m.getProperty("includeMaximum", Boolean.class).orElse(false));

        } else  if (node instanceof VariableReadNode){
            VariableReadNode v = (VariableReadNode)node;

            VariableRange typeRange = forType(v.getVariableInfo().getTypeVariable().getTypeDefinition());
            
            return new VariableRange (
                    v.getVariableInfo().getMinimum().orElse(typeRange.min),
                    v.getVariableInfo().getMaximum().orElse(typeRange.max),
                    v.getVariableInfo().isIncludeMaximum());

        } else if (node instanceof RangeNode){
            RangeNode range = (RangeNode) node;

            BigDecimal min = null;
            BigDecimal max = null;
            if (range.getStart() instanceof NumericValue) {
                min = (BigDecimal)((NumericValue) range.getStart()).getValue();
            }

            if (range.getEnd() instanceof NumericValue) {
                max = (BigDecimal)((NumericValue) range.getEnd()).getValue();
            }

            return new VariableRange (min, max, range.isIncludeEnd());
        } else if (node instanceof TypeNode){
            return forType(((TypeNode) node).getTypeVariable().getTypeDefinition());
        } else if (node instanceof BoxingPointNode){
            return extractFrom(node.getFirstChild());
        } else if (node instanceof NewInstanceCreationNode
                && ((NewInstanceCreationNode) node).getArguments() != null 
                && ((NewInstanceCreationNode) node).getArguments().getChildren().size() == 1
                ){
            return extractFrom(((NewInstanceCreationNode) node).getArguments().getFirstChild().getFirstChild());
        }

        return EMPTY;
    }

    private final BigDecimal max;
    private final BigDecimal min;
    private final boolean includeMax;

    private VariableRange(BigDecimal min, BigDecimal max, boolean includeMax) {
        super();
        this.max = max;
        this.min = min;
        this.includeMax = includeMax;
    }

    public Optional<BigDecimal> getMax() {
        return Optional.ofNullable(max);
    }


    public Optional<BigDecimal> getMin() {
        return Optional.ofNullable(min);
    }

    public boolean contains(BigDecimal value){
        if (this.min != null && this.max != null){
            return value.compareTo(min) >= 0 && value.compareTo(max) <=0;
        }
        return false;
    }

    public boolean contains(VariableRange other) {
         if (this.min == null && this.max == null){
             return true;
         } else if (other.min == null && other.max == null){
             return false;
         }
         
         BigDecimal inf = min(this.min , other.min);
         BigDecimal maj = min(this.max , other.max);
         
         return (inf == null || inf.equals(this.min)) && maj.equals(other.max);
    }
    
    public boolean isIncludeMax() {
        return includeMax;
    }

    public VariableRange operate(VariableRange other, ArithmeticOperation op) {
        switch (op){
        case Division:
            return new VariableRange(  
                    div (this.min, other.max, -1),
                    div (this.max, other.min, +1),
                    this.isIncludeMax() && other.isIncludeMax());
        case IntegerDivision:
            return new VariableRange(  
                    intdiv (this.min, other.max),
                    intdiv (this.max, other.min),
                    this.isIncludeMax() && other.isIncludeMax());
        case Power:
            return EMPTY;
        case Addition:
            return new VariableRange( 
                    this.getMin().flatMap(m -> other.getMin().map( o -> m.add(o))).orElse(null),
                    this.getMax().flatMap(m -> other.getMax().map( o -> m.add(o))).orElse(null),
                    this.isIncludeMax() && other.isIncludeMax()
                    );  
        case Subtraction:
            return new VariableRange( 
                    this.getMin().flatMap(m -> other.getMax().map( o -> m.subtract(o))).orElse(null),
                    this.getMax().flatMap(m -> other.getMin().map( o -> m.subtract(o))).orElse(null),
                    this.isIncludeMax() && other.isIncludeMax()
                    );
        case Multiplication:
            return new VariableRange( 
                    this.getMin().flatMap(m -> other.getMin().map( o -> m.multiply(o))).orElse(null),
                    this.getMax().flatMap(m -> other.getMax().map( o -> m.multiply(o))).orElse(null),
                    this.isIncludeMax() && other.isIncludeMax());
        case WrapAddition:
        case WrapMultiplication:
        case WrapSubtraction:
        case BitAnd:
        case BitOr:
        case BitXor:
        case LeftShift:
        case RightShift:
        case SignedRightShift:
            return new VariableRange(  
                    min (this.min, other.min),
                    min (this.max, other.max),
                    this.isIncludeMax() && other.isIncludeMax());
      
        case Complement:
        case Decrement:
        case Increment:
        case Positive:
            return this;
        case Remainder:
            // always contained in the "other" range
            return other;
        default:
            return EMPTY;
        }

    }

    private BigDecimal div(BigDecimal a, BigDecimal b, int i) {
        if (a == null || b == null){
            return null;
        }
        
        return a.divideAndRemainder(b)[0].add(BigDecimal.valueOf(i));
    }

    private BigDecimal intdiv(BigDecimal a, BigDecimal b) {
       if (a == null || b == null){
           return null;
       }
       
       return a.divideAndRemainder(b)[0];
    }

    private BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null){
            return b;
        }
        
        if (b == null){
           return a;
        }
        
        return a.compareTo(b) <= 0 ? a : b;
    }




}
