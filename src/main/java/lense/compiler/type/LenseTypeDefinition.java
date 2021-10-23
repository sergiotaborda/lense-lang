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
import java.util.stream.Stream;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class LenseTypeDefinition  implements TypeDefinition {

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
        if (this.genericParameters.stream().anyMatch(it -> it == null)){
        	throw new RuntimeException();
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
        if (this.genericParameters.stream().anyMatch(it -> it == null)){
        	throw new RuntimeException();
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

    public boolean isSpecified() {
    	return specificationOrigin != null;
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
                    // consider Association<K,V> implements Assortment<KeyValue<K,V>>
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

    public void updateFrom(TypeDefinition o, TypeAssistant typeAssistant){
        if (o != this){
            LenseTypeDefinition other = (LenseTypeDefinition)o;
            if (this.kind == null) {
                this.kind = other.kind;
            }
      
            addDifferent(this.members, other.members);
            addDifferent(this.interfaces,other.interfaces); // TODO reset generics 
         
            for (TypeVariable a : other.genericParameters){
            	
            	boolean found = false;
            	int i =0;
            	for (; i < this.genericParameters.size(); i++){
            		TypeVariable b = this.genericParameters.get(i);
            		
					if (typeAssistant.isAssignableTo(a, b).matches()) {
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
    	if (this == other) {
    		return true;
    	}
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
    
    @Override
	public String getPackageName() {
    	 int pos = name.lastIndexOf('.');
         if (pos >= 0) {
             return name.substring(0,pos);
         } else {
             return "";
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
    	
    	Optional<Integer> index = Optional.ofNullable(genericParametersMapping.get(typeName));
    	
//    	if (!index.isPresent() && this.superDefinition!= null) {
//    		index = ((LenseTypeDefinition)this.superDefinition).getGenericParameterIndexBySymbol(typeName);
//    	}
//    	
//    	if (!index.isPresent()) {
//    		for( TypeDefinition n : this.interfaces) {
//        		index = ((LenseTypeDefinition)n).getGenericParameterIndexBySymbol(typeName);
//        		if (index.isPresent()) {
//        			return index;
//        		}
//        	}
//    	}

        return index;
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
    public void addField(String name, TypeVariable typeDefinition, Imutability imutabilityValue, Visibility visibility) {


        final Field field = new Field(name, typeDefinition, imutabilityValue == Imutability.Imutable);
        field.setDeclaringType(this);
        field.setVisibility(visibility);
        
        // fields are unique by name
        this.members.remove(field);
        this.members.add(field);

    }


    public Property addProperty(String name, lense.compiler.type.variable.TypeVariable type ,Visibility visibility, boolean canRead, boolean canWrite) {

        if ( name == null){
            throw new IllegalArgumentException("Name is mandatory");
        }
        final Property property = new Property(this, name, type, canRead, canWrite);

        property.setVisibility(visibility);
        
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



    public IndexerProperty addIndexer(lense.compiler.type.variable.TypeVariable type , Visibility visibility, boolean canRead, boolean canWrite , lense.compiler.type.variable.TypeVariable[] params) {

        final IndexerProperty property = new IndexerProperty(this, type, canRead, canWrite, params);

        property.setVisibility(visibility);
        
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

    public void addIndexer(TypeDefinition type , Visibility visibility, boolean canRead, boolean canWrite, lense.compiler.type.variable.TypeVariable[] params) {
        addIndexer( type, visibility, canRead, canWrite,params);
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
        Optional<Field> member = this.getMembers().stream().filter(m -> m.isField() && m.getName().equals(name))
                .map(m -> (Field) m).findAny();

        if (!member.isPresent() && this.superDefinition != null) {
            member = this.superDefinition.getFieldByName(name);
        }
        
        if (!member.isPresent() && this.getInterfaces() != null) {
        	 for ( TypeDefinition it : this.getInterfaces()) {
                 var field = it.getFieldByName(name);
                 if (field.isPresent()) {
                	 return field;
                 }
             }
        }

        return member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Property> getPropertyByName(String name) {
        Optional<Property> member = this.getMembers().stream().filter(m -> m.isProperty() && m.getName().equals(name))
                .map(m -> (Property) m).findAny();

        if (!member.isPresent() && this.superDefinition != null) {
            member = this.superDefinition.getPropertyByName(name);
        }
        
        if (!member.isPresent() && this.getInterfaces() != null) {
       	 for ( TypeDefinition it : this.getInterfaces()) {
                var property = it.getPropertyByName(name);
                if (property.isPresent()) {
               	 return property;
                }
            }
       }

        return member;
    }



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
		}
		
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
