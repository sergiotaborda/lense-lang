/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;

import lense.compiler.type.TypeDefinition;

/**
 * 
 */
public interface TypeVariable {

	public String getName();

	public IntervalTypeVariable toIntervalTypeVariable();

	public List<IntervalTypeVariable> getGenericParameters();

	public TypeDefinition getTypeDefinition();
	

    TypeVariable changeBaseType(TypeDefinition concrete);

	public boolean isSingleType();
    
}