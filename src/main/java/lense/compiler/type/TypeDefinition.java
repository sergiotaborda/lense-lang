/**
 * 
 */
package lense.compiler.type;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public interface TypeDefinition {

	
	public String getName();

	public String getSimpleName();
	
	public TypeKind getKind();
	
	public List<TypeMember> getMembers();
	
	public TypeDefinition getSuperDefinition();

	public List<IntervalTypeVariable> getGenericParameters();

	public Optional<Constructor> getConstructorByParameters(MethodParameter ... parameters);
		
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

	public Collection<TypeDefinition> getInterfaces();

	public void updateFrom(TypeDefinition type);

	public boolean isGeneric();

	public Optional<IndexerProperty> getIndexerPropertyByTypeArray(TypeVariable[] type);


}
