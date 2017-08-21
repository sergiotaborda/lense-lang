package lense.compiler.crosscompile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class PrimitiveTypeDefinition implements TypeDefinition {

	
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
	public List<IntervalTypeVariable> getGenericParameters() {
		return Collections.emptyList();
	}

	@Override
	public Optional<Constructor> getConstructorByParameters(ConstructorParameter... parameters) {
		return Optional.empty();
	}

	@Override
	public Optional<Constructor> getConstructorByPromotableParameters(ConstructorParameter... parameters) {
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

}
