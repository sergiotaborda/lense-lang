package lense.compiler.crosscompile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.crosscompile.java.JavaTypeKind;
import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.Field;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.Method;
import lense.compiler.type.MethodSignature;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

public class PrimitiveTypeDefinition implements TypeDefinition {

	public static final PrimitiveTypeDefinition BOOLEAN = new PrimitiveTypeDefinition("boolean");
	
	private String name;

	public PrimitiveTypeDefinition(String name){
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSimpleName() {
		return name;
	}

	@Override
	public TypeKind getKind() {
		return JavaTypeKind.Primitive;
	}

	@Override
	public List<TypeMember> getMembers() {
		return Collections.emptyList();
	}

	@Override
	public TypeDefinition getSuperDefinition() {
		return null;
	}

	@Override
	public List<TypeVariable> getGenericParameters() {
		return Collections.emptyList();
	}

	@Override
	public Optional<Constructor> getConstructorByParameters(Visibility visibility,ConstructorParameter... parameters) {
		return Optional.empty();
	}

	@Override
	public Optional<Constructor> getConstructorByPromotableParameters(Visibility visibility, ConstructorParameter... parameters) {
		return Optional.empty();
	}

	@Override
	public Optional<Field> getFieldByName(String name) {
		return Optional.empty();
	}

	@Override
	public Optional<Property> getPropertyByName(String fieldName) {
		return Optional.empty();
	}

	@Override
	public Collection<Method> getMethodsByName(String string) {
		return Collections.emptyList();
	}

	@Override
	public Optional<Method> getMethodBySignature(MethodSignature signature) {
		return Optional.empty();
	}

	@Override
	public Optional<Method> getMethodByPromotableSignature(MethodSignature signature) {
		return Optional.empty();
	}

	@Override
	public List<TypeDefinition> getInterfaces() {
		return Collections.emptyList();
	}

	@Override
	public void updateFrom(TypeDefinition type) {
		// no-op
	}

	@Override
	public boolean isGeneric() {
		return false;
	}

	@Override
	public Optional<IndexerProperty> getIndexerPropertyByTypeArray(TypeVariable[] type) {
		return Optional.empty();
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public Collection<TypeMember> getAllMembers() {
		return this.getMembers();
	}

	@Override
	public TypeVariable getLowerBound() {
		return this;
	}

	@Override
	public TypeVariable getUpperBound() {
		return this;
	}

	@Override
	public Variance getVariance() {
		return Variance.Invariant;
	}

	@Override
	public Optional<String> getSymbol() {
		return Optional.empty();
	}

	@Override
	public TypeDefinition getTypeDefinition() {
		return this;
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return this;
	}

	@Override
	public boolean isSingleType() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return true;
	}

	@Override
	public boolean isCalculated() {
		return false;
	}

	@Override
	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
		// no-op
	}

	@Override
	public boolean isFinal() {
		return true;
	}

}
