/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.Method;
import lense.compiler.type.MethodSignature;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.typesystem.Variance;

/**
 * 
 * 
 */
public class MethodFreeTypeVariable extends CalculatedTypeVariable implements TypeMemberAwareTypeVariable {

	private Method method;
	protected int parameterIndex;

	public MethodFreeTypeVariable (int parameterIndex){
		this.parameterIndex = parameterIndex;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getSymbol() {
		return original().getSymbol();
	}
	
	protected TypeVariable original(){
		return method.getFreeGenericTypes().get(parameterIndex);
	}
	
	public String toString(){
		return getUpperBound().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Variance getVariance() {
		return Variance.Invariant;
	}
	
	/**
	 * @param signature
	 */
	public TypeVariable bindGenerics(MethodSignature signature) {
		return signature.getParameters().get(parameterIndex).getType();
	}

	@Override
	public void setDeclaringMember(TypeMember member) {
		this.method = (Method)member;
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		throw new UnsupportedOperationException();
	}

    @Override
    public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
        //no-op
    }



	



}
