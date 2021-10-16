package lense.compiler.crosscompile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.Field;
import lense.compiler.type.Property;
import lense.compiler.type.TypeAssistant;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Variance;

public final class ErasedTypeDefinition implements TypeDefinition{

    final TypeDefinition originalType;
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
    
    public ErasedTypeDefinition getTypeDefinition(){
        return this;
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

  
    public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
        originalType.ensureNotFundamental(convert);
    }


    public Optional<Field> getFieldByName(String name) {
        return originalType.getFieldByName(name);
    }

    public Optional<Property> getPropertyByName(String fieldName) {
        return originalType.getPropertyByName(fieldName);
    }


    public List<TypeDefinition> getInterfaces() {
        return originalType.getInterfaces();
    }

    public void updateFrom(TypeDefinition type, TypeAssistant typeAssistant) {
        originalType.updateFrom(type,typeAssistant);
    }

    public boolean isGeneric() {
        return originalType.isGeneric();
    }

    public boolean isAlgebric() {
        return originalType.isAlgebric();
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

	@Override
	public String getPackageName() {
		return originalType.getPackageName();
	}



    
}
