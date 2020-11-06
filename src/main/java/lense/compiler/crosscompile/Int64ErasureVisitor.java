package lense.compiler.crosscompile;

import lense.compiler.context.SemanticContext;
import lense.compiler.typesystem.LenseTypeSystem;

public class Int64ErasureVisitor extends AbstractPrimitiveIntegerErasureVisitor {

    public Int64ErasureVisitor (SemanticContext ctx ){
    	super(ctx,LenseTypeSystem.Int64(), PrimitiveTypeDefinition.LONG);
    }
    
   

}
