/**
 * 
 */
package lense.compiler.type.variable;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.typesystem.Variance;

/**
 * The type follows a type in the declaring type
 */
public class TypeMemberDeclaringTypeVariable extends CalculatedTypeVariable implements TypeMemberAwareTypeVariable {

	
	private TypeMember member;
	private int parameterIndex;

	public TypeMemberDeclaringTypeVariable (TypeMember member, int index){
		this.parameterIndex = index;
		this.member = member;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		 return original().getName();
	}
	
	public String toString(){
		return original().getName();
	}

	@Override
	protected IntervalTypeVariable original() {
		if (parameterIndex > member.getDeclaringType().getGenericParameters().size() - 1){
			throw new IndexOutOfBoundsException("Memebr " + member.getDeclaringType() + " as no generic type index " +  parameterIndex);
		}
		return member.getDeclaringType().getGenericParameters().get(parameterIndex);
	}

	@Override
	public void setDeclaringMember(TypeMember member) {
		this.member = member;
	}


	@Override
	public Variance getVariance() {
		return original().getVariance();
	}


	@Override
	public IntervalTypeVariable changeBaseType(TypeDefinition concrete) {
		return new TypeMemberDeclaringTypeVariable(this.member, parameterIndex);
	}


}
