/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class LenseTypeDefinition implements TypeDefinition {

    private String name;
    private TypeKind kind;
    private List<TypeMember> members = new CopyOnWriteArrayList<TypeMember>();
    private List<TypeDefinition> interfaces = new ArrayList<TypeDefinition>();
    protected List<TypeVariable> genericParameters = new ArrayList<>();
    protected Map<String , Integer> genericParametersMapping = new HashMap<>();
    private TypeDefinition superDefinition;
    private boolean isAbstract;
    private boolean isAlgebric;
    private boolean isNative;
    private Visibility visibility;
    private boolean plataformSpecific;
	private LenseTypeDefinition specificationOrigin = null;
    private boolean isFinal = false;
    
    private List<TypeDefinition> caseValues = Collections.emptyList();
    private List<TypeDefinition> caseTypes = Collections.emptyList();
	private boolean isImmutable;

    public LenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition) {
        this.name = name;
        this.kind = kind;
        this.superDefinition = superDefinition;
        if (superDefinition == null){
            this.genericParameters =  new ArrayList<>(0);
        } else {
            this.genericParameters = new ArrayList<>(superDefinition.getGenericParameters());
            this.genericParametersMapping = new HashMap<>(superDefinition.genericParametersMapping);
        }
    }

    public LenseTypeDefinition(String name, TypeKind kind, LenseTypeDefinition superDefinition, List<TypeVariable> parameters) {
        this.name = name;
        this.kind = kind;
        this.superDefinition = superDefinition;
        this.genericParameters = new ArrayList<>(parameters);

        int i =0;
        for (TypeVariable param : parameters){
        	if (param.getSymbol().isPresent()) {
        		this.genericParametersMapping.put(param.getSymbol().get(), i);
        	}
        	i++;
        }
    }

    protected LenseTypeDefinition() {
    }
    
    protected LenseTypeDefinition duplicate() {
    	return copyTo(new LenseTypeDefinition());
    }
    
    public final LenseTypeDefinition copyTo(LenseTypeDefinition other) {
    	   other.name = this.name;
    	   other.kind = this.kind;
    	   other.superDefinition = this.superDefinition;
    	   other.isAbstract = this.isAbstract;
    	   other.isNative = this.isNative;
    	   other.isFinal = this.isFinal;
    	   other.visibility = this.visibility;
    	   other.plataformSpecific = this.plataformSpecific;
    	   other.isAlgebric = this.isAlgebric;
    	   other.caseTypes = this.caseTypes;
    	   other.caseValues = this.caseValues;
    	   other.isImmutable = this.isImmutable;
    	   
           return other;
    }

    public LenseTypeDefinition specify(List<TypeVariable> genericParameters) {
        LenseTypeDefinition concrete = this.duplicate();

        if (this.getGenericParameters().size() != genericParameters.size()){
            throw new IllegalArgumentException("Specific parameters count  must match generic parameters count on " + this.name + ". Expected " + this.getGenericParameters().size() + ". Found " + genericParameters.size());
        }

        concrete.specificationOrigin = this;
        
        concrete.genericParameters = new ArrayList<>(genericParameters);
        concrete.genericParametersMapping = new HashMap<>(this.genericParametersMapping);

        concrete.addMembers(this.members.stream().map(m -> m.changeDeclaringType(concrete)));

        for (TypeDefinition interfaceType : this.interfaces) {

            if (interfaceType.isGeneric()) {
                List<TypeVariable> binded;
                
                if ( interfaceType.getGenericParameters().size() == genericParameters.size()){
                    binded = new ArrayList<>(genericParameters);

                } else {
                    // TODO this is not correct because genericParameters are not used property. 
                    // consider Association<K,V> implements Assortement<KeyValue<K,V>>
                    binded = new ArrayList<>(interfaceType.getGenericParameters().size());

                    for (TypeVariable p :  interfaceType.getGenericParameters()) {
                        binded.add(p.changeBaseType(concrete));
                    }
                    
                }
                
                TypeDefinition sp = ((LenseTypeDefinition)interfaceType).specify(binded);
               

                concrete.addInterface(sp);
            } else {
                concrete.addInterface(interfaceType);
            }

        }

        // TODO super type
        
        return concrete;
    }


    public boolean isPlataformSpecific() {
        return plataformSpecific;
    }

    public void setPlataformSpecific(boolean plataformSpecific) {
        this.plataformSpecific = plataformSpecific;
    }

    public boolean isExplicitlyImmutable() {
        return isImmutable;
    }

    public void setExplicitlyImmutable(boolean isImmutable) {
        this.isImmutable = isImmutable;
    }
    
    public boolean isImmutable() {
    	return isImmutable || this.kind.isValue();
    }

    
    public boolean isFundamental(){
        return false;
    }

    public void updateFrom(TypeDefinition o){
        if (o != this){
            LenseTypeDefinition other = (LenseTypeDefinition)o;
            if (this.kind == null) {
                this.kind = other.kind;
            }
      
            addDifferent(this.members, other.members);
            addDifferent(this.interfaces,other.interfaces); // TODO reset generics 
         
            LenseTypeSystem instance = LenseTypeSystem.getInstance();
            for (TypeVariable a : other.genericParameters){
            	
            	boolean found = false;
            	int i =0;
            	for (; i < this.genericParameters.size(); i++){
            		TypeVariable b = this.genericParameters.get(i);
            		
					if (instance.isAssignableTo(a, b)) {
						found = true;
						break;
					}
            	}
            	
            
                if (!found){
                	this.genericParameters.add(a);
                }
            }
            
            genericParametersMapping = new HashMap<>();

            int i =0;
            for (TypeVariable param : genericParameters){
            	if (param.getSymbol().isPresent()) {
            		this.genericParametersMapping.put(param.getSymbol().get(), i);
            	}
            	i++;
            }

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
        for (TypeVariable p : genericParameters) {
            builder.append(p.getSymbol().orElse(p.getTypeDefinition().getName())).append(",");
        }
        builder.delete(builder.length() - 1, builder.length());

        return builder.append(">").toString();
    }

    public boolean equals(Object other){
        return other instanceof LenseTypeDefinition && equals((LenseTypeDefinition)other);
    }

    public boolean equals(LenseTypeDefinition other){
        if ( this.getName().equals(other.getName()) && this.genericParameters.size() == other.genericParameters.size()) {
        	
        	Iterator<TypeVariable> itA = this.genericParameters.iterator();
         	Iterator<TypeVariable> itB = other.genericParameters.iterator();
         	while (itA.hasNext()) {
         		TypeVariable a = itA.next();
         		TypeVariable b = itB.next();
         		
         		if (!a.equals(b)) {
         			return false;
         		}
         	}
         	
         	return true;
        	
        }
        return false;
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
    public List<TypeVariable> getGenericParameters() {
        return Collections.unmodifiableList(genericParameters);
    }



    public Optional<Integer> getGenericParameterIndexBySymbol(String typeName) {
        return Optional.ofNullable(genericParametersMapping.get(typeName));
    }
    
    public Optional<String> getGenericParameterSymbolByIndex(int index) {
        for(Entry<String, Integer> pair : genericParametersMapping.entrySet()){
            if (pair.getValue().intValue() == index){
                return Optional.of(pair.getKey());
            }
        }
        return Optional.empty();
    }


    /**
     * @param name TODO
     * 
     */
    public Constructor addConstructor(String name, ConstructorParameter... parameters) {
        return addConstructor(false, name, parameters);
    }

    public Constructor addConstructor(boolean implicit, String name, ConstructorParameter... parameters) {
        Constructor m = new Constructor(name,Arrays.asList(parameters), implicit,Visibility.Public);
        addConstructor(m);
        return m;
    }

    public void addConstructor(Constructor m) {
        m.setDeclaringType(this);
        this.members.add(m);
    }

    public Method addMethod( String name, TypeDefinition returnType, MethodParameter... parameters) {
        return addMethod(Visibility.Public,name, new MethodReturn(returnType), parameters);
    }

    /**
     * @param name2
     * @param typeDefinition
     * @param parameters
     */
    public Method addMethod(Visibility visibility, String name, TypeDefinition returnType, MethodParameter... parameters) {
        return addMethod(visibility,name, new MethodReturn(returnType), parameters);
    }

    public Method addMethod(Visibility visibility,String name, MethodReturn returnType, MethodParameter... parameters) {
        Method m = new Method(false,visibility, name, returnType, parameters);
    	addMethod(m);
    	
    	return m;

    }

    public void addMethod (Method m){

        if (this.kind == LenseUnitKind.Interface){
            m.setAbstract(true);
        }
        
        if (m.getDeclaringType() == null) {
        	m.setDeclaringType(this);
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


    public Property addProperty(String name, lense.compiler.type.variable.TypeVariable type , boolean canRead, boolean canWrite) {

        if ( name == null){
            throw new IllegalArgumentException("Name is mandatory");
        }
        final Property property = new Property(this, name, type, canRead, canWrite);

        if (type instanceof TypeMemberAwareTypeVariable){
            ((TypeMemberAwareTypeVariable)type).setDeclaringMember(property);
        }
        addProperty(property);
        
        return property;
    }

    public void addProperty(Property property) {
        // fields are unique by name
        this.members.remove(property);
        this.members.add(property);
    }



    public IndexerProperty addIndexer(lense.compiler.type.variable.TypeVariable type , boolean canRead, boolean canWrite , lense.compiler.type.variable.TypeVariable[] params) {

        final IndexerProperty property = new IndexerProperty(this, type, canRead, canWrite, params);

        if (type instanceof TypeMemberAwareTypeVariable){
            ((TypeMemberAwareTypeVariable)type).setDeclaringMember(property);
        }

        addIndexer(property);
        
        return property;

    }

    public void addIndexer(IndexerProperty property) {
        // fields are unique by name
        this.members.remove(property);
        this.members.add(property);
    }

    public void addIndexer(TypeDefinition type , boolean canRead, boolean canWrite, lense.compiler.type.variable.TypeVariable[] params) {
        addIndexer( type, canRead, canWrite,params);
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
            if (!LenseTypeSystem.getInstance().isAssignableTo(params[i], indexes[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * @param superType
     */
    public void setSuperTypeDefinition(TypeDefinition superType) {

        if (((LenseTypeDefinition)superType).isPlataformSpecific()){
            throw new RuntimeException("Class cannot be plataform specific (" + superType + ")");
        }

        if (this == superType || this.equals(superType)){
            throw new IllegalArgumentException("Class cannot be supertype of it self");
        }

        this.superDefinition = superType;
    

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
            all = this.superDefinition.getMethodsByName(name);
        }
        
        return all;
    }

    
    public Optional<Method> getDeclaredMethodBySignature(MethodSignature signature) {
    	
        if (signature.getName() == null) {
            throw new IllegalArgumentException("Signature must have a name");
        }

        // find exact local

    	Method mostSpecific = null;
        for (TypeMember m : resolveMembers()) {
            if (m.isMethod() && signature.getName().equals(m.getName()) && LenseTypeSystem.isSignatureImplementedBy(signature, (CallableMember<Method>) m) ) {
            	 
            	if (mostSpecific == null) {
            		mostSpecific = (Method) m;
            	} else {
            		mostSpecific = mostSpecific(mostSpecific,(Method) m);
            	}
            }
        }
        
        return Optional.ofNullable(mostSpecific);
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
        Optional<Method> method = getDeclaredMethodBySignature(signature);
        
        if (method.isPresent()) {
        	return method;
        }

        // find exact upper class
        if (this.superDefinition != null && !this.superDefinition.equals(this)) {
           method = this.superDefinition.getMethodBySignature(signature);

            if (method.isPresent()){
                Method myMethod = method.get().changeDeclaringType(this);
               // this.addMethod(myMethod);

                return Optional.of(myMethod);
            }
        }

        for (TypeDefinition i : this.interfaces){
            Optional<Method> m =  i.getMethodByPromotableSignature(signature);
            if (m.isPresent()){
                Method myMethod = m.get().changeDeclaringType(this);
              //  this.addMethod(myMethod);

                return Optional.of(myMethod);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Method> getMethodByPromotableSignature(MethodSignature signature) {
        // find promotable

    	Method mostSpecific = null;
    	
    	outter: for (Method mth : this.getMethodsByName(signature.getName())) {
            if (mth.getParameters().size() == signature.getParameters().size()) {
            	
            	if (signature.getParameters().isEmpty()) {
            		   return Optional.of(mth);
            	}
            	
                for (int p = 0; p < signature.getParameters().size(); p++) {
                    CallableMemberMember<Method> mp = signature.getParameters().get(p);
                    if (!LenseTypeSystem.getInstance().isPromotableTo(mp.getType(), mth.getParameters().get(p).getType())) {
                    	continue outter;
                    }
                }
                
                if (mostSpecific == null) {
                	mostSpecific = mth;
                } else {
                	mostSpecific = mostSpecific(mostSpecific , mth);
                }
              
            }
        }
    	
    	if (mostSpecific != null) {
        	return Optional.of(mostSpecific);
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
	public Optional<Constructor> getConstructorByName(String name , ConstructorParameter... parameters) {
        // find exact local

        List<CallableMemberMember<Constructor>> list = Arrays.asList(parameters);
        Stream<Constructor> map = members.stream()
                .filter(m -> m.isConstructor())
                .map(m -> (Constructor) m);
        
    	if (name != null) {
			map = map.filter(c -> c.getName().equals(name));
		}
    	
		return map.filter(c -> LenseTypeSystem.areSignatureParametersImplementedBy(list, c.getParameters())  ).findAny();
	}
	
    @Override
    public Optional<Constructor> getConstructorByParameters(ConstructorParameter... parameters) {
    	return getConstructorByName(null, parameters);
    }
	
	@Override
	public Optional<Constructor> getConstructorByNameAndPromotableParameters(String name , ConstructorParameter... parameters) {
		return searchConstructorWithPromotableParameters(name, null, parameters);
	}
	
	private Optional<Constructor> searchConstructorWithPromotableParameters(String name , Boolean implicit, ConstructorParameter... parameters) {
		
		Stream<Constructor> map = members.stream().filter(m -> m.isConstructor()).map(m -> (Constructor)m);
		if (name != null) {
			map = map.filter(c -> c.getName().equals(name));
		}
		
		if (implicit != null) {
			map = map.filter(c -> c.isImplicit() == implicit.booleanValue());
		}
		Iterator<Constructor> iterator = map.iterator();
		Constructor mostSpecific = null;
        while(iterator.hasNext()){
            Constructor constructor = iterator.next();
            if (visibility != null) {
            	if (constructor.getVisibility() != visibility) {
            		continue;
            	}
            }
            if (constructor.getParameters().size() == parameters.length) {
                for (int p = 0; p < parameters.length; p++) {
                    ConstructorParameter mp = parameters[p];
                    if (LenseTypeSystem.getInstance().isPromotableTo(mp.getType(), constructor.getParameters().get(p).getType())) {
                    	if (mostSpecific == null) {
                    		mostSpecific = constructor;
                    	} else {
                    		mostSpecific = mostSpecific(mostSpecific, constructor);
                    	}
                       
                    }
                }
            }
        }
        return Optional.ofNullable(mostSpecific);
	}
	
    private <C extends CallableMember<C>> C mostSpecific(C mostSpecific, C constructor) {
    	 
    	 for (int p = 0; p < constructor.getParameters().size(); p++) {
              CallableMemberMember<C> mp = constructor.getParameters().get(p);
              CallableMemberMember<C> sp = mostSpecific.getParameters().get(p);
              
             if (!LenseTypeSystem.isAssignableTo(sp.getType(),mp.getType())) {
            	 return constructor;
                
             }
         }
    	 return mostSpecific;
    	 
	}
    

	@Override
    public Optional<Constructor> getConstructorByPromotableParameters(ConstructorParameter... parameters) {
    	return getConstructorByNameAndPromotableParameters(null, parameters);
    }

	@Override
	public Optional<Constructor> getConstructorByImplicitAndPromotableParameters(boolean implicit, ConstructorParameter... parameters) {
		return searchConstructorWithPromotableParameters(null, implicit, parameters);
	}


    /**
     * 
     */
    public void addMembers(Stream<TypeMember> all) {

        all.forEach(a -> members.add(a));

    }

    public void addInterface(TypeDefinition other) {
    	
    	if (other.getName().isEmpty()) {
    		throw new IllegalArgumentException("Types must have a name");
    	}
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



    @Override
    public Collection<TypeMember> getAllMembers() {

        Set<TypeMember> members = new HashSet<>(this.getMembers());

        if (this.getSuperDefinition() != null) {
            members.addAll(this.getSuperDefinition().getAllMembers());
        }

        for ( TypeDefinition it : this.getInterfaces()) {
            members.addAll(it.getAllMembers());
        }

        return members;
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
		if (this.getName().equals(concrete.getName())) {
			return concrete;
		} else {
			return this;
		}
	
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

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isAlgebric() {
		return isAlgebric;
	}

	public void setAlgebric(boolean isAlgebric) {
		this.isAlgebric = isAlgebric;
	}

	public List<TypeDefinition> getCaseTypes() {
		return caseTypes;
	}

	public void setCaseTypes(List<TypeDefinition> caseTypes) {
		this.caseTypes = caseTypes;
	}

	public List<TypeDefinition> getCaseValues() {
		return caseValues;
	}

	public void setCaseValues(List<TypeDefinition> caseValues) {
		this.caseValues = caseValues;
	}




























}
