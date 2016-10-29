/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class LenseTypeDefinition implements TypeDefinition {

	private String name;
	private TypeKind kind;
	private List<TypeMember> members = new ArrayList<TypeMember>();
	private List<TypeDefinition> interfaces = new ArrayList<TypeDefinition>();
	private List<IntervalTypeVariable> genericParameters;
	private Map<String , Integer> genericParametersMapping = new HashMap<>();
	private TypeDefinition superDefinition;
	private boolean isAbstract;
	private boolean isNative;
	private Visibility visibility;
	
	public LenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
		this.name = name;
		this.kind = kind;
		this.superDefinition = superDefinition;
		if (superDefinition == null){
			this.genericParameters =  new ArrayList<>(0);
		} else {
			this.genericParameters = superDefinition.getGenericParameters();
			this.genericParametersMapping.putAll(superDefinition.genericParametersMapping);
		}
	}

	public LenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition,
			IntervalTypeVariable... parameters) {
		this.name = name;
		this.kind = kind;
		this.superDefinition = superDefinition;
		this.genericParameters = new ArrayList<>( Arrays.asList(parameters));

		for (int i =0; i < parameters.length; i++){
			this.genericParametersMapping.put(parameters[i].getSymbol().orElse("_"), i);
		}
	}

	public void updateFrom(TypeDefinition o){
		if (o != this){
			LenseTypeDefinition other = (LenseTypeDefinition)o;
			this.kind = other.kind;
			addDifferent(this.members, other.members);
			addDifferent(this.interfaces,other.interfaces); // TODO reset generics 
			addDifferent(this.genericParameters, other.genericParameters);

			genericParametersMapping.putAll(other.genericParametersMapping);
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
			builder.append(p.getSymbol().orElse(p.getTypeDefinition().getName())).append(",");
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


	public void addGenericParameter(String genericParameterSymbol, TypeVariable typeVar) {
		IntervalTypeVariable var = typeVar.toIntervalTypeVariable();
		if (!genericParametersMapping.containsKey(genericParameterSymbol)){
			genericParameters.add(var);
			addGenericParametersMapping(genericParameterSymbol, genericParameters.size() - 1);
		}
	}

	private void addGenericParametersMapping(String genericParameterSymbol, int index) {
		genericParametersMapping.put(genericParameterSymbol, index);
	}

	public Optional<Integer> getGenericParameterIndexBySymbol(String typeName) {
		return Optional.ofNullable(genericParametersMapping.get(typeName));
	}

	/**
	 * @param name TODO
	 * 
	 */
	public Constructor addConstructor(String name, ConstructorParameter... parameters) {
		return addConstructor(false, name, parameters);
	}

	public Constructor addConstructor(boolean implicit, String name, ConstructorParameter... parameters) {
		Constructor m = new Constructor(name,Arrays.asList(parameters), implicit);
		addConstructor(m);
		return m;
	}

	public void addConstructor(Constructor m) {
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

		if (this.kind == LenseUnitKind.Interface){
			m.setAbstract(true);
		}
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

	public void addProperty(String name, TypeDefinition type , boolean canRead, boolean canWrite) {
		addProperty(name, new FixedTypeVariable(type), canRead, canWrite);
	}

	public void addProperty(String name, lense.compiler.type.variable.TypeVariable type , boolean canRead, boolean canWrite) {

		if ( name == null){
			throw new IllegalArgumentException("Name is mandatory");
		}
		final Property property = new Property(this, name, type, canRead, canWrite);

		if (type instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)type).setDeclaringMember(property);
		}
		addProperty(property);
	}

	public void addProperty(Property property) {
		// fields are unique by name
		this.members.remove(property);
		this.members.add(property);
	}



	public void addIndexer(lense.compiler.type.variable.TypeVariable type , boolean canRead, boolean canWrite , lense.compiler.type.variable.TypeVariable[] params) {

		final IndexerProperty property = new IndexerProperty(this, type, canRead, canWrite, params);

		if (type instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)type).setDeclaringMember(property);
		}

		addIndexer(property);

	}

	public void addIndexer(IndexerProperty property) {
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
	public void setKind(LenseUnitKind kind) {
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
			LenseTypeSystem typeSystem = LenseTypeSystem.getInstance();
			if (m.isMethod() && signature.getName().equals(m.getName())
					&& typeSystem.isSignatureImplementedBy(signature, (CallableMember<Method>) m)) {
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
					CallableMemberMember<Method> mp = signature.getParameters().get(p);
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
	public Optional<Constructor> getConstructorByPromotableParameters(ConstructorParameter... parameters) {

		Iterator<Constructor> iterator = members.stream().filter(m -> m.isConstructor()).map(m -> (Constructor)m).iterator();
		while(iterator.hasNext()){
			Constructor constructor = iterator.next();
			if (constructor.getParameters().size() == parameters.length) {
				for (int p = 0; p < parameters.length; p++) {
					ConstructorParameter mp = parameters[p];
					if (LenseTypeSystem.getInstance().isPromotableTo(mp.getType(), constructor.getParameters().get(p).getType())) {
						return Optional.of(constructor);
					}
				}
			}
		}
		return Optional.empty();
	}


	@Override
	public Optional<Constructor> getConstructorByParameters(ConstructorParameter... parameters) {
		// find exact local

		List<CallableMemberMember<Constructor>> list = Arrays.asList(parameters);
		return members.stream()
				.filter(m -> m.isConstructor())
				.map(m -> (Constructor) m).filter(c -> LenseTypeSystem.getInstance().areSignatureParametersImplementedBy(list, c.getParameters())  ).findAny();

	}

	/**
	 * 
	 */
	public void addMembers(Stream<TypeMember> all) {

		all.forEach(a -> members.add(a));

	}

	public void addInterface(TypeDefinition other) {
//		if (!other.getKind().equals(LenseUnitKind.Interface)) {
//			throw new RuntimeException("Type " + other.getName()  +" is not an interface");
//		}

		for(Iterator<TypeDefinition> it = interfaces.iterator(); it.hasNext(); ){
			
			if (it.next().getName().equals(other.getName())){
				it.remove();
				break;
			}
		}

		interfaces.add(other);
	}

	@Override
	public List<TypeDefinition> getInterfaces() {
		return interfaces;
	}

	@Override
	public boolean isGeneric() {
		return !this.genericParametersMapping.isEmpty();
	}

	public boolean hasConstructor() {
		return members.stream().anyMatch(m -> m.isConstructor());
	}

	public Stream<Constructor> getConstructors() {
		return members.stream().filter(m -> m.isConstructor()).map(m -> (Constructor)m);
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isNative() {
		return isNative;
	}
	
	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public String getGenericParameterSymbolByIndex(int index) {
		for(Entry<String, Integer> pair : genericParametersMapping.entrySet()){
			if (pair.getValue().intValue() == index){
				return pair.getKey();
			}
		}
		return null;
	}


















}
