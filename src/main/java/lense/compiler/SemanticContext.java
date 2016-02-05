/**
 * 
 */
package lense.compiler;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lense.compiler.repository.UpdatableTypeRepository;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeNotFoundException;
import compiler.typesystem.TypeSearchParameters;
import compiler.typesystem.VariableInfo;

/**
 * 
 */
public class SemanticContext {

	Deque<SemanticScope> scopes = new LinkedList<SemanticScope>();
	private List<String> imports = new ArrayList<String>();
	
	private UpdatableTypeRepository resolver;
	private String currentpackage;
	
	public SemanticContext(UpdatableTypeRepository resolver, String currentpackage){
		this.resolver = resolver;
		this.currentpackage= currentpackage;
		
		imports.add(currentpackage);
	}
	
	public String getCurrentPackageName(){
		return currentpackage;
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
		return scopes.isEmpty() ? null : scopes.getFirst();
	}

	/**
	 * @param name
	 */
	public void addImportPackage(String importName) {
		imports.add(importName);
	}

	/**
	 * @param name
	 * @param genericParametersCount
	 * @return
	 */
	public TypeDefinition typeForName(String name, int genericParametersCount) {
	
		Optional<TypeDefinition> type = resolveTypeForName(name, genericParametersCount);
		
		if (!type.isPresent()){
			throw new TypeNotFoundException(name + "'" + genericParametersCount +" is not a recognized type");
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
			return typeForQualifiedName(name,genericParametersCount);

		} else {

			// try type variable
			final SemanticScope currentScope = currentScope();
			if (currentScope != null){
				VariableInfo variableInfo = currentScope.searchVariable(name);

				if (variableInfo != null && variableInfo.isTypeVariable()){
					return Optional.of(variableInfo.getTypeDefinition());
				}


				// not type variable, attach imports and look again

				for (String importPackage : imports){
					Optional<TypeDefinition> type = typeForQualifiedName(importPackage + "." + name, genericParametersCount);
					if (type.isPresent()){
						return type;
					}
				}
				
				return typeForQualifiedName(name,genericParametersCount );
			}
		
			return Optional.empty();
		}

	}

	/**
	 * @param name
	 * @return
	 */
	private Optional<TypeDefinition> typeForQualifiedName(String name, int genericParametersCount) {

		TypeSearchParameters filter = new TypeSearchParameters(name,  genericParametersCount);
		
		return resolver.resolveType(filter);

	}

	/**
	 * @param i 
	 * @param name
	 * @param myType
	 */
	public void registerType(TypeDefinition type, int genericParametersCount) {
		resolver.registerType(type);
	}

	/**
	 * @param name
	 * @return
	 */
	public Optional<TypeDefinition> resolvePackageForName(String name) {
		return Optional.empty();
	}



}
