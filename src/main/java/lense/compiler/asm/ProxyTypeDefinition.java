package lense.compiler.asm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.Field;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

public class ProxyTypeDefinition extends LenseTypeDefinition {

	private LenseTypeDefinition originalType;
	

	private String name;
	
	public ProxyTypeDefinition(String name) {
		this.name = name;
	}
	
	
	public void setOriginal(LenseTypeDefinition other) {
		this.originalType = other;
	}
	
	private LenseTypeDefinition original() {
		if(originalType == null) {
			throw new RuntimeException("Dependency not set");
		}
		
		return originalType;
	}

	public List<TypeVariable> getGenericParameters() {
		return originalType == null ? Collections.emptyList() : originalType.getGenericParameters();
	}
	
	public LenseTypeDefinition specify(List<TypeVariable> genericParameters) {
		return original().specify(genericParameters);
	}

	public boolean isPlataformSpecific() {
		return this.originalType == null ? false : originalType.isPlataformSpecific();
	}

	public void setPlataformSpecific(boolean plataformSpecific) {
		original().setPlataformSpecific(plataformSpecific);
	}


	public boolean isExplicitlyImmutable() {
		return original().isExplicitlyImmutable();
	}


	public void setExplicitlyImmutable(boolean isImmutable) {
		original().setExplicitlyImmutable(isImmutable);
	}


	public boolean isImmutable() {
		return original().isImmutable();
	}


	public boolean isFundamental() {
		return original().isFundamental();
	}


	public String toString() {
		return this.name;
	}


	public boolean equals(Object other) {
		return original().equals(other);
	}


	public boolean equals(LenseTypeDefinition other) {
		return original().equals(other);
	}


	public int hashCode() {
		return original().hashCode();
	}


	public Optional<Integer> getGenericParameterIndexBySymbol(String typeName) {
		return original().getGenericParameterIndexBySymbol(typeName);
	}


	public Optional<String> getGenericParameterSymbolByIndex(int index) {
		return original().getGenericParameterSymbolByIndex(index);
	}


	public Constructor addConstructor(String name, ConstructorParameter... parameters) {
		return original().addConstructor(name, parameters);
	}


	public Constructor addConstructor(boolean implicit, String name, ConstructorParameter... parameters) {
		return original().addConstructor(implicit, name, parameters);
	}


	public void addConstructor(Constructor m) {
		original().addConstructor(m);
	}


	public Method addMethod(String name, TypeDefinition returnType, MethodParameter... parameters) {
		return original().addMethod(name, returnType, parameters);
	}


	public Method addMethod(Visibility visibility, String name, TypeDefinition returnType,
			MethodParameter... parameters) {
		return original().addMethod(visibility, name, returnType, parameters);
	}


	public Method addMethod(Visibility visibility, String name, MethodReturn returnType,
			MethodParameter... parameters) {
		return original().addMethod(visibility, name, returnType, parameters);
	}


	public void addMethod(Method m) {
		original().addMethod(m);
	}


	public void addField(String name, TypeVariable typeDefinition, Imutability imutabilityValue) {
		original().addField(name, typeDefinition, imutabilityValue);
	}


	public Property addProperty(String name, TypeVariable type, boolean canRead, boolean canWrite) {
		return original().addProperty(name, type, canRead, canWrite);
	}


	public void addProperty(Property property) {
		original().addProperty(property);
	}


	public IndexerProperty addIndexer(TypeVariable type, boolean canRead, boolean canWrite, TypeVariable[] params) {
		return original().addIndexer(type, canRead, canWrite, params);
	}


	public void addIndexer(IndexerProperty property) {
		original().addIndexer(property);
	}


	public void addIndexer(TypeDefinition type, boolean canRead, boolean canWrite, TypeVariable[] params) {
		original().addIndexer(type, canRead, canWrite, params);
	}


	public void setSuperTypeDefinition(TypeDefinition superType) {
		original().setSuperTypeDefinition(superType);
	}


	public void setKind(LenseUnitKind kind) {
		original().setKind(kind);
	}

	public void addMembers(Stream<TypeMember> all) {
		original().addMembers(all);
	}


	public void addInterface(TypeDefinition other) {
		original().addInterface(other);
	}


	public boolean hasConstructor() {
		return original().hasConstructor();
	}


	public Stream<Constructor> getConstructors() {
		return original().getConstructors();
	}


	public void setAbstract(boolean isAbstract) {
		original().setAbstract(isAbstract);
	}


	public boolean isNative() {
		return original().isNative();
	}


	public void setNative(boolean isNative) {
		original().setNative(isNative);
	}


	public Visibility getVisibility() {
		return original().getVisibility();
	}


	public void setVisibility(Visibility visibility) {
		original().setVisibility(visibility);
	}


	public void setFinal(boolean isFinal) {
		original().setFinal(isFinal);
	}


	public void setAlgebric(boolean isAlgebric) {
		original().setAlgebric(isAlgebric);
	}


	public void setCaseTypes(List<TypeDefinition> caseTypes) {
		original().setCaseTypes(caseTypes);
	}


	public void setCaseValues(List<TypeDefinition> caseValues) {
		original().setCaseValues(caseValues);
	}

	public TypeVariable getLowerBound() {
		return original().getLowerBound();
	}

	public TypeVariable getUpperBound() {
		return original().getUpperBound();
	}

	public String getName() {
		return name;
	}

	public Variance getVariance() {
		return original().getVariance();
	}

	public String getSimpleName() {
		return original().getSimpleName();
	}

	public Optional<String> getSymbol() {
		return Optional.of(name);
	}

	public TypeKind getKind() {
		return original().getKind();
	}

	public List<TypeMember> getMembers() {
		return original().getMembers();
	}

	public Collection<TypeMember> getAllMembers() {
		return original().getAllMembers();
	}

	public TypeDefinition getTypeDefinition() {
		return original().getTypeDefinition();
	}

	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return original().changeBaseType(concrete);
	}

	public boolean isSingleType() {
		return original().isSingleType();
	}

	public TypeDefinition getSuperDefinition() {
		return original().getSuperDefinition();
	}

	public boolean isFixed() {
		return original().isFixed();
	}

	public boolean isCalculated() {
		return original().isCalculated();
	}


	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
		original().ensureNotFundamental(convert);
	}

	public Optional<Field> getFieldByName(String name) {
		return original().getFieldByName(name);
	}

	public Optional<Property> getPropertyByName(String fieldName) {
		return original().getPropertyByName(fieldName);
	}

	public List<TypeDefinition> getInterfaces() {
		return original().getInterfaces();
	}

	public void updateFrom(TypeDefinition type) {
		//no-op
	}

	public boolean isGeneric() {
		return original().isGeneric();
	}

	public boolean isAlgebric() {
		return original().isAlgebric();
	}

	public boolean isAbstract() {
		return original().isAbstract();
	}

	public boolean isFinal() {
		return original().isFinal();
	}

	public List<TypeDefinition> getCaseValues() {
		return original().getCaseValues();
	}

	public List<TypeDefinition> getCaseTypes() {
		return original().getCaseTypes();
	}

	public List<TypeDefinition> getAllCases() {
		return original().getAllCases();
	} 
}
