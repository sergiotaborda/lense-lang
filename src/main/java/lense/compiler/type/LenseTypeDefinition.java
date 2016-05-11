/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lense.compiler.ast.Imutability;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 */
public class LenseTypeDefinition implements TypeDefinition {

	private String name;
	private TypeKind kind;
	private List<TypeMember> members = new ArrayList<TypeMember>();
	private List<TypeDefinition> interfaces = new ArrayList<TypeDefinition>();
	private List<IntervalTypeVariable> genericParameters;
	private TypeDefinition superDefinition;

	public LenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
		this.name = name;
		this.kind = kind;
		this.superDefinition = superDefinition;
		this.genericParameters = superDefinition == null ? new ArrayList<>(0)
				: superDefinition.getGenericParameters();
	}

	public LenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition,
			IntervalTypeVariable... parameters) {
		this.name = name;
		this.kind = kind;
		this.superDefinition = superDefinition;
		this.genericParameters = new ArrayList<>( Arrays.asList(parameters));
	}
	
	public void updateFrom(TypeDefinition o){
		if (o != this){
			LenseTypeDefinition other = (LenseTypeDefinition)o;
			this.kind = other.kind;
			addDifferent(this.members, other.members);
			addDifferent(this.interfaces,other.interfaces); // TODO reset generics 
			addDifferent(this.genericParameters, other.genericParameters);
			this.superDefinition = other.superDefinition;
		}
	}

	private static <T> void addDifferent (List<T> original, List<T> others){
		for (T t : others){
			int pos = original.indexOf(t);
			if (pos >=0){
				original.set(pos, t);
			} else {
				original.add(t);
			}
		}
	}
	

	public String toString() {

		if (genericParameters.isEmpty()) {
			return name;
		}

		StringBuilder builder = new StringBuilder(name).append("<");
		for (IntervalTypeVariable p : genericParameters) {
			if (p.getUpperbound() == null) {
				builder.append(p.getName()).append(",");
			} else {
				builder.append(p.getUpperbound().toString()).append(",");
			}

		}
		builder.delete(builder.length() - 1, builder.length());

		return builder.append(">").toString();
	}
	
	public boolean equals(Object other){
		return other instanceof LenseTypeDefinition && equals((LenseTypeDefinition)other);
	}
	
	public boolean equals(LenseTypeDefinition other){
		return this.getName().equals(other.getName()) && this.genericParameters.size() == other.genericParameters.size();
	}
	
	public int hashCode(){
		return this.name.hashCode();
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
		if (pos >= 0) {
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
	public List<IntervalTypeVariable> getGenericParameters() {
		return genericParameters;
	}

	/**
	 * 
	 */
	public void addConstructor(MethodParameter... parameters) {
		Constructor m = new Constructor(Arrays.asList(parameters), false);
		m.setDeclaringType(this);
		this.members.add(m);
	}
	
	public void addImplicitConstructor(MethodParameter... parameters) {
		Constructor m = new Constructor(Arrays.asList(parameters), true);
		m.setDeclaringType(this);
		this.members.add(m);
	}

	/**
	 * @param name2
	 * @param typeDefinition
	 * @param parameters
	 */
	public void addMethod(String name, TypeDefinition returnType, MethodParameter... parameters) {
		addMethod(name, new MethodReturn(new FixedTypeVariable(returnType)), parameters);
	}

	public void addMethod(String name, MethodReturn returnType, MethodParameter... parameters) {
		addMethod(new Method(name, returnType, parameters));
		
	}
	
	public void addMethod (Method m){
		
		
		m.setDeclaringType(this);

		if (!members.isEmpty()) {
			Optional<Method> previous = getMethodBySignature(new MethodSignature(name, m.getParameters()));
			if (previous.isPresent()) {
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
	public void addField(String name, TypeVariable typeDefinition, Imutability imutabilityValue) {
		
		
		final Field field = new Field(name, typeDefinition, imutabilityValue == Imutability.Imutable);
		field.setDeclaringType(this);

		// fields are unique by name
		this.members.remove(field);
		this.members.add(field);
	
	}
	
	public void addProperty(String name, lense.compiler.type.variable.TypeVariable type , boolean canRead, boolean canWrite) {
		
		if ( name == null){
			throw new IllegalArgumentException("Name is mandatory");
		}
		final Property property = new Property(this, name, type, canRead, canWrite);
	
		if (type instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)type).setDeclaringMember(property);
		}
		// fields are unique by name
		this.members.remove(property);
		this.members.add(property);
	}

	public void addProperty(String name, TypeDefinition type , boolean canRead, boolean canWrite) {
		addProperty(name, new FixedTypeVariable(type), canRead, canWrite);
	}

	public void addIndexer(lense.compiler.type.variable.TypeVariable type , boolean canRead, boolean canWrite , lense.compiler.type.variable.TypeVariable[] params) {

		final IndexerProperty property = new IndexerProperty(this, type, canRead, canWrite, params);

		if (type instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)type).setDeclaringMember(property);
		}
		
		// fields are unique by name
		this.members.remove(property);
		this.members.add(property);
	}
	
	public void addIndexer(TypeDefinition type , boolean canRead, boolean canWrite, lense.compiler.type.variable.TypeVariable[] params) {
		addIndexer( new FixedTypeVariable(type), canRead, canWrite,params);
	}
	
	@Override
	public Optional<IndexerProperty> getIndexerPropertyByTypeArray(TypeVariable[] params) {

		Optional<IndexerProperty> member = resolveMembers().stream().filter(m -> m.isIndexer() && indexesAreEqual(((IndexerProperty)m).getIndexes(), params))
				.map(m -> (IndexerProperty) m).findAny();

		if (!member.isPresent() && this.superDefinition != null) {
			return this.superDefinition.getIndexerPropertyByTypeArray(params);
		}

		return member;
	}



	private boolean indexesAreEqual(TypeVariable[] indexes, TypeVariable[] params) {
		if( indexes.length != params.length) {
			return false;
		}
		
		for (int i = 0; i < indexes.length; i++){
			// TODO use TypeVariables
			if (!LenseTypeSystem.isAssignableTo(params[i], indexes[i])){
				return false;
			}
		}
		return true;
	}

	/**
	 * @param superType
	 */
	public void setSuperTypeDefinition(TypeDefinition superType) {
		if (!this.equals(superType)){
			this.superDefinition = superType;
			if (this.genericParameters.size() == 0) {
				this.genericParameters = superType.getGenericParameters();
			}
		} else {
			throw new RuntimeException("Class cannot be supertype of it self");
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
		Optional<Field> member = resolveMembers().stream().filter(m -> m.isField() && m.getName().equals(name))
				.map(m -> (Field) m).findAny();

		if (!member.isPresent() && this.superDefinition != null) {
			return this.superDefinition.getFieldByName(name);
		}

		return member;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Property> getPropertyByName(String name) {
		Optional<Property> member = resolveMembers().stream().filter(m -> m.isProperty() && m.getName().equals(name))
				.map(m -> (Property) m).findAny();

		if (!member.isPresent() && this.superDefinition != null) {
			return this.superDefinition.getPropertyByName(name);
		}

		return member;
	}

	private List<TypeMember> resolveMembers(){
		return  members;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Method> getMethodsByName(String name) {
		Collection<Method> all = resolveMembers().stream().filter(m -> m.isMethod() && m.getName().equals(name))
				.map(m -> (Method) m).collect(Collectors.toList());

		if (all.isEmpty() && this.superDefinition != null) {
			return this.superDefinition.getMethodsByName(name);
		}

		return all;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Method> getMethodBySignature(MethodSignature signature) {

		if (signature.getName() == null) {
			throw new IllegalArgumentException("Signature must have a name");
		}

		// find exact local
		for (TypeMember m : resolveMembers()) {
			if (m.isMethod() && signature.getName().equals(m.getName())
					&& LenseTypeSystem.isSignatureImplementedBy(signature, (CallableMember) m)) {
				return Optional.of((Method) m);
			}
		}

		// find exact upper class
		if (this.superDefinition != null && !this.superDefinition.equals(this)) {
			Optional<Method> method = this.superDefinition.getMethodBySignature(signature);
			
			if (method.isPresent()){
				Method myMethod = method.get().changeDeclaringType(this);
				this.addMethod(myMethod);
				
				return Optional.of(myMethod);
			}
		}
		
		for (TypeDefinition i : this.interfaces){
			Optional<Method> m =  i.getMethodByPromotableSignature(signature);
			if (m.isPresent()){
				Method myMethod = m.get().changeDeclaringType(this);
				this.addMethod(myMethod);
				
				return Optional.of(myMethod);
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<Method> getMethodByPromotableSignature(MethodSignature signature) {
		// find promotable

		for (Method mth : this.getMethodsByName(signature.getName())) {
			if (mth.getParameters().size() == signature.getParameters().size()) {
				for (int p = 0; p < signature.getParameters().size(); p++) {
					MethodParameter mp = signature.getParameters().get(p);
					if (LenseTypeSystem.getInstance().isPromotableTo(mp.getType(), mth.getParameters().get(p).getType())) {
						return Optional.of(mth);
					}
				}
			}
		}

		if (this.getSuperDefinition() != null){
			Optional<Method> m = this.getSuperDefinition().getMethodByPromotableSignature(signature);
			if (m.isPresent()){
				return m;
			}
		}
		
		for (TypeDefinition i : this.interfaces){
			Optional<Method> m =  i.getMethodByPromotableSignature(signature);
			if (m.isPresent()){
				return m;
			}
		}
		
		return Optional.empty();
	}

	@Override
	public Optional<Constructor> getConstructorByParameters(MethodParameter... parameters) {
		// find exact local
		MethodSignature constructorSignature = new MethodSignature("", parameters);

		Optional<Constructor> member = members.stream()
				.filter(m -> m.isConstructor() && LenseTypeSystem.getInstance()
						.isSignatureImplementedBy(constructorSignature, (CallableMember) m))
				.map(m -> (Constructor) m).findAny();

		return member;
	}

	/**
	 * 
	 */
	public void addMembers(Stream<TypeMember> all) {
		
		all.forEach(a -> members.add(a));
		
	}

	public void addInterface(TypeDefinition other) {
		if (!other.getKind().equals(Kind.Interface)) {
			throw new RuntimeException("Type " + other.getName()  +" is not an interface");
		}
		
		interfaces.add(other);
	}

	@Override
	public Collection<TypeDefinition> getInterfaces() {
		return interfaces;
	}

	@Override
	public boolean isGeneric() {
		return !this.genericParameters.isEmpty() && this.genericParameters.stream().anyMatch(p -> !p.getLowerBound().equals(p.getUpperbound()));
	}





}