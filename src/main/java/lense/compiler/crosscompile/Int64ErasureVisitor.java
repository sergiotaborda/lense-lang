package lense.compiler.crosscompile;

import lense.compiler.typesystem.LenseTypeSystem;

public class Int64ErasureVisitor extends AbstractPrimitiveIntegerErasureVisitor {

    public Int64ErasureVisitor (){
    	super(LenseTypeSystem.Int64(), PrimitiveTypeDefinition.LONG);
    }
    
   

}
