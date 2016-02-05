/**
 * 
 */
package lense.compiler.typesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lense.compiler.ast.Imutability;

import compiler.typesystem.CallableMember;
import compiler.typesystem.Constructor;
import compiler.typesystem.Field;
import compiler.typesystem.FixedTypeVariable;
import compiler.typesystem.GenericTypeParameter;
import compiler.typesystem.Method;
import compiler.typesystem.MethodParameter;
import compiler.typesystem.MethodReturn;
import compiler.typesystem.MethodSignature;
import compiler.typesystem.Property;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeKind;
import compiler.typesystem.TypeMember;

/**
 * 
 */
public class LenseTypeDefinition implements TypeDefinition {

	private String name;
	private TypeKind kind;
	private List<TypeMember> members= new ArrayList<TypeMember>();
	private List<GenericTypeParameter> genericParameters;
	private TypeDefinition superDefinition;

	public LenseTypeDefinition (String name, TypeKind kind, LenseTypeDefinition superDefinition){
		this.name = name;
		this.kind = kind;
		this.superDefinition = superDefinition;
		this.genericParameters = superDefinition == null ? Collections.emptyList() : superDefinition.getGenericParameters();
	}

	public LenseTypeDefinition (String name, TypeKind kind, LenseTypeDefinition superDefinition, GenericTypeParameter ... parameters){
		this.name = name;
		this.kind = kind;
		this.superDefinition = superDefinition;
		this.genericParameters = Arrays.asList(parameters);
	}

	public String toString(){
		
		if (genericParameters.isEmpty()){
			return name;
		}
		
		StringBuilder builder = new StringBuilder(name).append("<");
		for(GenericTypeParameter p : genericParameters){
			if (p.getUpperbound() == null){
				builder.append("lense.core.lang.Any").append(",");
			} else {
				builder.append(p.getUpperbound().toString()).append(",");
			}
			
		}
		builder.delete(builder.length()-1, builder.length());
		
		return builder.append(">").toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSimpleName() {
		int pos = name.lastIndexOf('.');
		if (pos >=0){
			return name.substring(pos + 1);
		} else {
			return name;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeKind getKind() {
		return kind;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TypeMember> getMembers() {
		return members;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition getSuperDefinition() {
		return superDefinition;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GenericTypeParameter> getGenericParameters() {
		return genericParameters;
	}


	/**
	 * 
	 */
	public void addConstructor(MethodParameter ... parameters) {
		Constructor m = new Constructor(Arrays.asList(parameters));
		m.setDeclaringType(this);
		this.members.add(m);
	}

	/**
	 * @param name2
	 * @param typeDefinition
	 * @param parameters
	 */
	public void addMethod(String name, TypeDefinition returnType, MethodParameter ... parameters) {
		addMethod(name, new MethodReturn(new FixedTypeVariable(returnType)), parameters);
	}
	
	public void addMethod(String name, MethodReturn returnType, MethodParameter ... parameters) {
		Method m = new Method(name, returnType, parameters);
		m.setDeclaringType(this);
		
		if (!members.isEmpty())
		{
			Optional<Method> previous = getMethodBySignature(new MethodSignature(name, parameters));
			if (previous.isPresent()){
				this.members.remove(previous.get());
			}
		}
		
		this.members.add(m);
	}

	/**
	 * @param name2
	 * @param typeDefinition
	 * @param imutabilityValue
	 */
	public void addField(String name, TypeDefinition typeDefinition,Imutability imutabilityValue) {
		final Field field = new Field(name, typeDefinition,imutabilityValue == Imutability.Imutable);
		field.setDeclaringType(this);
		
		// fields are unique by name
		
		this.members.remove(field);
		this.members.add(field);
	}

	/**
	 * @param superType
	 */
	public void setSuperTypeDefinition(TypeDefinition superType) {
		this.superDefinition =  superType;
		if (this.genericParameters.size() == 0){
			this.genericParameters = superType.getGenericParameters();
		}
	}


	/**
	 * @param kind2
	 */
	public void setKind(Kind kind) {
		this.kind = kind;
	}




	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Field> getFieldByName(String name) {
		Optional<Field> member = members.stream().filter(m -> m.isField() && m.getName().equals(name)).map(m -> (Field)m).findAny();

		if (!member.isPresent() && this.superDefinition != null){
			return this.superDefinition.getFieldByName(name);
		}

		return member;
	}




	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Property> getPropertyByName(String fieldName) {
		Optional<Property> member = members.stream().filter(m -> m.isProperty() && m.getName().equals(name)).map(m -> (Property)m).findAny();

		if (!member.isPresent() && this.superDefinition != null){
			return this.superDefinition.getPropertyByName(name);
		}

		return member;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Method> getMethodsByName(String name) {
		Collection<Method>  all = members.stream().filter(m -> m.isMethod() && m.getName().equals(name)).map(m -> (Method)m).collect(Collectors.toList());

		if (all.isEmpty() && this.superDefinition != null){
			return this.superDefinition.getMethodsByName(name);
		}

		return all;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Method> getMethodBySignature(MethodSignature signature) {
		
		if (signature.getName() == null){
			throw new IllegalArgumentException("Signature must have a name");
		}
	
		// find exact local
		for (TypeMember m :  members){
			if (m.isMethod() && signature.getName().equals(m.getName()) && LenseTypeSystem.isSignatureImplementedBy(signature, (CallableMember)m)){
				return Optional.of((Method)m);
			}
		}
		
		// find exact upper class
		if (this.superDefinition != null){
			return this.superDefinition.getMethodBySignature(signature);
		}

		return Optional.empty();
	}
	@Override
	public Optional<Method> getMethodByPromotableSignature(MethodSignature signature)
	{
		// find promotable

		for(Method mth : this.getMethodsByName(signature.getName())){
			if (mth.getParameters().size() == signature.getParameters().size()){
				for (int p =0; p < signature.getParameters().size(); p++){
					MethodParameter mp = signature.getParameters().get(p);
					if (LenseTypeSystem.getInstance().isPromotableTo(mp.getType().getUpperbound(), mth.getParameters().get(p).getType().getLowerBound())){
						return Optional.of(mth);
					}
				}
			}
		}

		return Optional.empty();
	}
	
	
	@Override
	public Optional<Constructor> getConstructorByParameters(MethodParameter ... parameters) {
		// find exact local
		MethodSignature constructorSignature = new MethodSignature("", parameters);
		
		Optional<Constructor>  member = members.stream().filter(m -> m.isConstructor() && LenseTypeSystem.getInstance().isSignatureImplementedBy(constructorSignature, (CallableMember)m)).map(m -> (Constructor)m).findAny();

		return member;
	}
	/**
	 * 
	 */
	void addMembers(Stream<TypeMember> all) {
		all.forEach(a -> members.add(a) );
	}

}
