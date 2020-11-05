package lense.compiler.crosscompile;

import lense.compiler.context.SemanticContext;
import lense.compiler.typesystem.LenseTypeSystem;

public class Int32ErasureVisitor extends AbstractPrimitiveIntegerErasureVisitor {

    public Int32ErasureVisitor (SemanticContext ctx ){
    	super(ctx,LenseTypeSystem.Int32(), PrimitiveTypeDefinition.INT);
    }
    
   

}
