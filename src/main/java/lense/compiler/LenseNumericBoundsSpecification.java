package lense.compiler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import compiler.lexer.ScanPositionHolder;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class LenseNumericBoundsSpecification {
	
     record NumericBounds (TypeDefinition type, BigDecimal min, BigDecimal max){
    	 
    	 public boolean contain(BigDecimal value) {
    		 if (min != null && value.compareTo(min) < 0) {
    			 return false;
    		 } else  if (max != null && value.compareTo(max) > 0) {
    			 return false;
    		 }
    		 
    		 return true;
    	 }
     };
	
     static List<NumericBounds> bounds = List.of(
    		 new NumericBounds(LenseTypeSystem.Int64(), BigDecimal.valueOf(Long.MIN_VALUE),BigDecimal.valueOf( Long.MAX_VALUE)),
    		 new NumericBounds(LenseTypeSystem.Int32(), BigDecimal.valueOf(Integer.MIN_VALUE),BigDecimal.valueOf( Integer.MAX_VALUE)),
    		 new NumericBounds(LenseTypeSystem.Natural(), BigDecimal.ZERO,null)
     );
     
     static TypeDefinition typeFromValue(BigDecimal value) {
    	 
    	 if (!whole(value)) {
    		 return LenseTypeSystem.Rational();
    	 }
    	 
    	 if (value.signum() < 0) {
    		 return LenseTypeSystem.Integer();
    	 }
    	 
    	 return LenseTypeSystem.Natural();
	}
     
     public static void checkInBounds(ScanPositionHolder holder, TypeVariable type, BigDecimal value) {
    	 if (type == null) {
    		 return;
    	 }
    	 for (var bound : bounds ) {
    		 if (bound.type.getName().equals(type.getTypeDefinition().getName())) {
    			 if (bound.min != null && value.compareTo(bound.min) < 0) {
    				 throw new CompilationError(holder, "Value " + value.toString() + " is too small to be hold by a " + type.toString() + "(Expected minimum : "+ bound.min + ")");
    			 } else if (bound.max != null && value.compareTo(bound.max) > 0) {
    				 throw new CompilationError(holder, "Value " + value.toString() + " is too large to be hold by a " + type.toString() + "(Expected maximum : "+ bound.max + ")");
    			 }
    		 }
    	 }
     }
     

	private static boolean whole(BigDecimal value) {
		return value.signum() == 0 
				|| value.scale() <= 0 
				|| value.stripTrailingZeros().scale() <= 0;
	}

}
