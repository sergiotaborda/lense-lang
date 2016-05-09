/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.typesystem.Variance;

/**
 * The type follows a type in the declaring type
 */
public class TypeMemberDeclaringTypeVariable implements TypeMemberAwareTypeVariable {

	
	private TypeMember member;
	private int parameterIndex;

	public TypeMemberDeclaringTypeVariable (int index){
		this.parameterIndex = index;
	}
	
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


	private IntervalTypeVariable original() {
		return member.getDeclaringType().getGenericParameters().get(parameterIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeVariable getUpperbound() {
		return original().getUpperbound();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeVariable getLowerBound() {
		return original().getLowerBound();
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
		return new TypeMemberDeclaringTypeVariable(parameterIndex);
	}


	@Override
	public IntervalTypeVariable toIntervalTypeVariable() {
		 return this;
	}


	@Override
	public List<IntervalTypeVariable> getGenericParameters() {
		return original().getGenericParameters();
	}


	@Override
	public TypeDefinition getTypeDefinition() {
		return original().getTypeDefinition();
	}

	@Override
	public boolean isSingleType() {
		return this.original().isSingleType();
	}


}
