/**
 * 
 */
package lense.compiler.type;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public interface TypeDefinition extends TypeVariable {

	public String getName();
	
	public default Optional<String> getTypeName(){
		return Optional.of(getName());
	}

	public String getSimpleName();
	
	public TypeKind getKind();
	
	/**
	 * 
	 * @return members of the type 
	 */
	public List<TypeMember> getMembers();
	
	/**
	 * 
	 * @return members of the type, including ones defined in super classes or interfaces
	 */
	public Collection<TypeMember> getAllMembers();
	
	public TypeDefinition getSuperDefinition();

	public List<TypeVariable> getGenericParameters();

	public List<Match<Constructor>> getConstructorByParameters(ConstructorParameter ... parameters);
	
	public List<Match<Constructor>> getConstructorByParameters(Visibility visbility, ConstructorParameter ... parameters);
	
	public Optional<Constructor> getConstructorByPromotableParameters(ConstructorParameter ... parameters);
	
	public List<Match<Constructor>> getConstructorByName(String name, ConstructorParameter ... parameters);
	
	public Optional<Constructor> getConstructorByNameAndPromotableParameters(String name,ConstructorParameter ... parameters);
	
	public Optional<Constructor> getConstructorByImplicitAndPromotableParameters(boolean implicit, ConstructorParameter ... parameters);
	
	
	/**
	 * @param name
	 * @return
	 */
	public Optional<Field> getFieldByName(String name);
	
	/**
	 * @param fieldName
	 * @return
	 */
	public Optional<Property> getPropertyByName(String fieldName);

	/**
	 * @param string
	 * @return
	 */
	public Collection<Method> getMethodsByName(String string);

	/**
	 * @param signature
	 * @return
	 */
	public Optional<Method> getMethodBySignature(MethodSignature signature);
	
	public Optional<Method> getMethodByPromotableSignature(MethodSignature signature);

	public List<TypeDefinition> getInterfaces();

	public void updateFrom(TypeDefinition type);

	public boolean isGeneric();
	
	public boolean isAlgebric();

	public Optional<IndexerProperty> getIndexerPropertyByTypeArray(TypeVariable[] type);

	public boolean isAbstract();

	public boolean isFinal();

	public List<TypeDefinition> getCaseValues();

	public List<TypeDefinition> getCaseTypes();
	
	public default List<TypeDefinition> getAllCases(){
		LinkedList<TypeDefinition> list = new LinkedList<>(getCaseValues());
		list.addAll(getCaseTypes());
		
		return list;
	}





}
