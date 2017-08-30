package lense.compiler.typesystem;

import java.util.Collections;
import java.util.List;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.variable.IntervalTypeVariable;

public class FundamentalLenseTypeDefinition extends LenseTypeDefinition {

	public FundamentalLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
		super(name, kind, superDefinition);
	}

	public FundamentalLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition,IntervalTypeVariable... parameters) {
		super(name, kind, superDefinition,parameters);
	}
	
	public boolean isFundamental(){
        return true;
    }
//	   /**
//     * {@inheritDoc}
//     */
//    @Override
//    public List<IntervalTypeVariable> getGenericParameters() {
//        return Collections.unmodifiableList(super.getGenericParameters());
//    }
    
}
