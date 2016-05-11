/**
 * 
 */
package lense.compiler.type.variable;

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
	public String getName() {
		return original().getName();
	}
	
	protected IntervalTypeVariable original(){
		return method.getFreeGenericTypes().get(parameterIndex);
	}
	
	public String toString(){
		return getUpperbound().toString();
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
	public IntervalTypeVariable changeBaseType(TypeDefinition concrete) {
		throw new UnsupportedOperationException();
	}


}