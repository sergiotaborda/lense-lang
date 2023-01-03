package lense.compiler.typesystem;

import java.util.Arrays;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.variable.TypeVariable;

public class FundamentalLenseTypeDefinition extends LenseTypeDefinition {

	public FundamentalLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
		super(name, kind, superDefinition);
		setVisibility(Visibility.Public);
	}

	public FundamentalLenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition,TypeVariable... parameters) {
		super(name, kind, superDefinition, Arrays.asList(parameters));
		setVisibility(Visibility.Public);
	}

	public boolean isFundamental(){
        return true;
    }

	protected FundamentalLenseTypeDefinition() {
		super();
	}
	
    protected LenseTypeDefinition duplicate() {
	   return copyTo(new FundamentalLenseTypeDefinition());
	}
    
   
}
