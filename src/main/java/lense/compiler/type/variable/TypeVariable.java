/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

/**
 * 
 */
public interface TypeVariable {

	TypeVariable getLowerBound();

	TypeVariable getUpperBound();

	Variance getVariance();


	/**
	 * The generic type parameter name, like T or S.
	 * @return
	 */
	public Optional<String> getSymbol();

	public List<TypeVariable> getGenericParameters();

	public TypeDefinition getTypeDefinition();


	TypeVariable changeBaseType(TypeDefinition concrete);

	public boolean isSingleType();
	public boolean isFixed();
	public boolean isCalculated();

	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert);


}