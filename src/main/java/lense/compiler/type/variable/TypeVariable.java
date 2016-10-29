/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;

import lense.compiler.type.TypeDefinition;

/**
 * 
 */
public interface TypeVariable {

	/**
	 * The generic type parameter name, like T or S.
	 * @return
	 */
	public Optional<String> getSymbol();

	public IntervalTypeVariable toIntervalTypeVariable();

	public List<IntervalTypeVariable> getGenericParameters();

	public TypeDefinition getTypeDefinition();
	

    TypeVariable changeBaseType(TypeDefinition concrete);

	public boolean isSingleType();
	public boolean isFixed();
    
}