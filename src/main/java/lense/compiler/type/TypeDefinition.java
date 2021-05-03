/**
 * 
 */
package lense.compiler.type;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public interface TypeDefinition extends TypeVariable {

	public String getName();

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


	public List<TypeDefinition> getInterfaces();

	public void updateFrom(TypeDefinition type, TypeAssistant typeAssistant);

	public boolean isGeneric();
	
	public boolean isAlgebric();

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
