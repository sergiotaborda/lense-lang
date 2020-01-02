package lense.compiler.crosscompile;

import lense.compiler.typesystem.LenseTypeSystem;

public class Int32ErasureVisitor extends AbstractPrimitiveIntegerErasureVisitor {

    public Int32ErasureVisitor (){
    	super(LenseTypeSystem.Int32(), PrimitiveTypeDefinition.INT);
    }
    
   

}
