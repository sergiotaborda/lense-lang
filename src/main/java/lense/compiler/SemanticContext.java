/**
 * 
 */
package lense.compiler;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeNotFoundException;
import compiler.typesystem.TypeResolver;
import compiler.typesystem.TypeSearchParameters;
import compiler.typesystem.VariableInfo;

/**
 * 
 */
public class SemanticContext {

	Deque<SemanticScope> scopes = new LinkedList<SemanticScope>();
	private List<String> imports = new ArrayList<String>();
	
	private Map<TypeSearchParameters, TypeDefinition> types = new HashMap<>();
	private TypeResolver resolver;
	
	public SemanticContext(TypeResolver resolver){
		this.resolver = resolver;

		imports.add("lense.lang");
	}

	/**
	 * 
	 */
	public void beginScope(String name) {
		SemanticScope scope = scopes.peek();
		if (scope != null){
			scopes.addFirst(new SemanticScope(name, scope));
		} else {
			scopes.addFirst(new SemanticScope(name));
		}
	}

	public void endScope(){
		scopes.removeFirst();
	}

	public SemanticScope currentScope(){
		return scopes.getFirst();
	}

	/**
	 * @param name
	 */
	public void addImportPackage(String importName) {
		imports.add(importName);
	}

	public TypeDefinition typeForName(String name) {
		return typeForName(name, 0);
	}
	/**
	 * @param name
	 * @param size
	 * @return
	 */
	public TypeDefinition typeForName(String name, int size) {
	
		Optional<TypeDefinition> type = resolveTypeForName(name, size);
		
		if (!type.isPresent()){
			throw new TypeNotFoundException(name + "'" + size +" is not a recognized type");
		}
		
		return type.get();
	}
	/**
	 * @param name
	 * @return
	 */
	public Optional<TypeDefinition> resolveTypeForName(String name, int genericParametersCount) {

		if(name.contains(".")){
			// is qualified
			TypeDefinition type = typeForQualifiedName(name,genericParametersCount);
			if (type!= null){
				return Optional.of(type);
			} else {
				return Optional.empty();
			}
		} else {

			// try type variable
			VariableInfo variableInfo = currentScope().searchVariable(name);

			if (variableInfo != null && variableInfo.isTypeVariable()){
				return Optional.of(variableInfo.getTypeDefinition());
			}


			// not type variable, attach imports and look again

			for (String importPackage : imports){
				TypeDefinition type = typeForQualifiedName(importPackage + "." + name, genericParametersCount);
				if (type != null){
					return Optional.of(type);
				}
			}
			return Optional.empty();
		}

	}

	/**
	 * @param name
	 * @return
	 */
	private TypeDefinition typeForQualifiedName(String name, int genericParametersCount) {

		TypeSearchParameters filter = new TypeSearchParameters(name,  genericParametersCount);
		
		TypeDefinition type = types.get(filter);


		if (type == null){
			
			type = resolver.resolveTypeByName(filter);

			if (type != null){
				types.put(filter, type);
			}
		}
		return type;
	}

	/**
	 * @param i 
	 * @param name
	 * @param myType
	 */
	public void registerType(TypeDefinition type, int genericParametersCount) {
		types.put(new TypeSearchParameters(type.getName(),  genericParametersCount), type);
	}



}
