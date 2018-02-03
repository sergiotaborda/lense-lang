///**
// * 
// */
//package lense.compiler.type.variable;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//
//import lense.compiler.type.TypeDefinition;
//import lense.compiler.typesystem.Variance;
//
///**
// * 
// */
//public class InherintedTypeVariable extends CalculatedTypeVariable  {
//
//
//	private TypeDefinition declaringClass;
//	private int superTypeParameterIndex;
//	private Variance positionVariance;
//
//	public InherintedTypeVariable(TypeDefinition declaringClass, int superTypeParameterIndex,Variance positionVariance){
//		this.declaringClass = declaringClass;
//		this.superTypeParameterIndex = superTypeParameterIndex;
//		this.positionVariance = positionVariance;
//	}
//
//	protected TypeVariable original(){
//		return declaringClass.getSuperDefinition().getGenericParameters().get(superTypeParameterIndex);
//	}
//
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Variance getVariance() {
//		return positionVariance;
//	}
//
//
//	@Override
//	public TypeVariable changeBaseType(TypeDefinition concrete) {
//		return new InherintedTypeVariable(concrete, superTypeParameterIndex,positionVariance );
//	}
//
//	@Override
//	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
//		this.declaringClass = convert.apply(declaringClass);
//	}
//
//
//	@Override
//	public Optional<String> getSymbol() {
//		return original().getSymbol();
//	}
//
//	@Override
//	public List<TypeVariable> getGenericParameters() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public TypeDefinition getTypeDefinition() {
//		return original().getTypeDefinition();
//	}
//
//
//
//
//}
