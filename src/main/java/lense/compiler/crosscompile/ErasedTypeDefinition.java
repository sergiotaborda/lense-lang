package lense.compiler.crosscompile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

public final class ErasedTypeDefinition implements TypeDefinition{

    private final TypeDefinition originalType;
    private final PrimitiveTypeDefinition primitiveType;

    public ErasedTypeDefinition(TypeDefinition originalType, PrimitiveTypeDefinition primitiveType){
        this.primitiveType = primitiveType;
        this.originalType = originalType;
    }
    
    public PrimitiveTypeDefinition getPrimitiveType(){
        return primitiveType;
    }
    
    public boolean equals(Object other){
        return primitiveType.equals(other);
    }
    
    public int hashCode(){
        return primitiveType.hashCode();
    }
    
    public String toString(){
        return "ERRASED[" + originalType.toString()  + "]";
    }
    
    public String getName() {
        return originalType.getName();
    }

    public TypeVariable getLowerBound() {
        return originalType.getLowerBound();
    }

    public String getSimpleName() {
        return originalType.getSimpleName();
    }

    public TypeVariable getUpperBound() {
        return originalType.getUpperBound();
    }

    public TypeKind getKind() {
        return originalType.getKind();
    }

    public Variance getVariance() {
        return originalType.getVariance();
    }

    public List<TypeMember> getMembers() {
        return originalType.getMembers();
    }

    public Optional<String> getSymbol() {
        return originalType.getSymbol();
    }

    public Collection<TypeMember> getAllMembers() {
        return originalType.getAllMembers();
    }

    public TypeDefinition getTypeDefinition() {
        return originalType.getTypeDefinition();
    }

    public TypeVariable changeBaseType(TypeDefinition concrete) {
        return originalType.changeBaseType(concrete);
    }

    public TypeDefinition getSuperDefinition() {
        return originalType.getSuperDefinition();
    }

    public boolean isSingleType() {
        return originalType.isSingleType();
    }

    public List<TypeVariable> getGenericParameters() {
        return originalType.getGenericParameters();
    }

    public boolean isFixed() {
        return originalType.isFixed();
    }

    public boolean isCalculated() {
        return originalType.isCalculated();
    }

    public Optional<Constructor> getConstructorByParameters(ConstructorParameter... parameters) {
        return originalType.getConstructorByParameters(parameters);
    }

    public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
        originalType.ensureNotFundamental(convert);
    }

    public Optional<Constructor> getConstructorByPromotableParameters(ConstructorParameter... parameters) {
        return originalType.getConstructorByPromotableParameters(parameters);
    }

    public Optional<Constructor> getConstructorByName(String name, ConstructorParameter... parameters) {
        return originalType.getConstructorByName(name, parameters);
    }

    public Optional<Constructor> getConstructorByNameAndPromotableParameters(String name,
            ConstructorParameter... parameters) {
        return originalType.getConstructorByNameAndPromotableParameters(name, parameters);
    }

    public Optional<Constructor> getConstructorByImplicitAndPromotableParameters(boolean implicit,
            ConstructorParameter... parameters) {
        return originalType.getConstructorByImplicitAndPromotableParameters(implicit, parameters);
    }

    public Optional<Field> getFieldByName(String name) {
        return originalType.getFieldByName(name);
    }

    public Optional<Property> getPropertyByName(String fieldName) {
        return originalType.getPropertyByName(fieldName);
    }

    public Collection<Method> getMethodsByName(String string) {
        return originalType.getMethodsByName(string);
    }

    public Optional<Method> getMethodBySignature(MethodSignature signature) {
        return originalType.getMethodBySignature(signature);
    }

    public Optional<Method> getMethodByPromotableSignature(MethodSignature signature) {
        return originalType.getMethodByPromotableSignature(signature);
    }

    public List<TypeDefinition> getInterfaces() {
        return originalType.getInterfaces();
    }

    public void updateFrom(TypeDefinition type) {
        originalType.updateFrom(type);
    }

    public boolean isGeneric() {
        return originalType.isGeneric();
    }

    public boolean isAlgebric() {
        return originalType.isAlgebric();
    }

    public Optional<IndexerProperty> getIndexerPropertyByTypeArray(TypeVariable[] type) {
        return originalType.getIndexerPropertyByTypeArray(type);
    }

    public boolean isAbstract() {
        return originalType.isAbstract();
    }

    public boolean isFinal() {
        return originalType.isFinal();
    }

    public List<TypeDefinition> getCaseValues() {
        return originalType.getCaseValues();
    }

    public List<TypeDefinition> getCaseTypes() {
        return originalType.getCaseTypes();
    }

    public List<TypeDefinition> getAllCases() {
        return originalType.getAllCases();
    }

    
}
