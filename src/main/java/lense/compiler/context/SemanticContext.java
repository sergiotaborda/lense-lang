/**
 * 
 */
package lense.compiler.context;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.lexer.ScanPosition;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeNotFoundException;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class SemanticContext {

	Deque<SemanticScope> scopes = new LinkedList<SemanticScope>();
	private List<String> imports = new ArrayList<String>();
	
	private UpdatableTypeRepository resolver;
	private String currentpackage;
	private ClassTypeNode parentClassTypeNode;
	
	public SemanticContext(UpdatableTypeRepository resolver, String currentpackage, ClassTypeNode ct){
		this.resolver = resolver;
		this.currentpackage= currentpackage;
		this.parentClassTypeNode = ct;
		
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

	public TypeVariable typeForName(TypeNode typeNode) {
		return typeForName(typeNode.getScanPosition(), typeNode.getName(), typeNode.getTypeParametersCount());
	}
	
	/**
	 * @param name
	 * @param genericParametersCount
	 * @return
	 */
	public TypeVariable typeForName(ScanPosition scanPosition, String name, int genericParametersCount) {
	
		SemanticScope scope = this.currentScope();
		if (scope != null){
			VariableInfo varThis = scope.searchVariable("this");
			
			if (varThis != null && varThis.getTypeVariable().getSymbol().map(s -> s.equals(name)).orElse(false)){
				return varThis.getTypeVariable().getTypeDefinition();
			}
			
		}
		
		Optional<TypeVariable> type = resolveTypeForName(name, genericParametersCount);
		
		if (!type.isPresent()){
			if (LenseTypeSystem.Any().getName().equals(name)){
				return LenseTypeSystem.Any();
			} else if (LenseTypeSystem.Nothing().getName().equals(name)){
				return LenseTypeSystem.Nothing();
			} 
			throw new TypeNotFoundException(name + "'" + genericParametersCount +" is not a recognized type. Did you imported it ? (" + scanPosition.getCompilationUnit().getName() + "@" + scanPosition.getLineNumber() +"," + scanPosition.getColumnNumber() +")" );
		} else if (type.get().getGenericParameters().size() != genericParametersCount) {
		    throw new TypeNotFoundException(name + "'" + genericParametersCount +" is not a recognized type. Did you imported it ? (" + scanPosition.getCompilationUnit().getName() + "@" + scanPosition.getLineNumber() +"," + scanPosition.getColumnNumber() +")");
		}
		
		return type.get();
	}
	/**
	 * @param name
	 * @return
	 */
	public Optional<TypeVariable> resolveTypeForName(String name, int genericParametersCount) {

		if(name.contains(".")){
			// is qualified
			return typeForQualifiedName(name,genericParametersCount);

		} else {

			// try type variable
			final SemanticScope currentScope = currentScope();
			if (currentScope != null){
				VariableInfo variableInfo = currentScope.searchVariable(name);

				if (variableInfo != null && variableInfo.isTypeVariable()){
					return Optional.of(variableInfo.getTypeVariable());
				}


				// not type variable, attach imports and look again

				for (String importPackage : imports){
					Optional<TypeVariable> type = typeForQualifiedName(importPackage + "." + name, genericParametersCount);
					if (type.isPresent()){
						return type;
					}
				}
				for (lense.compiler.Import imp : parentClassTypeNode.imports()){
					if (imp.getMatchAlias().equals(name)){
						Optional<TypeVariable> type = typeForQualifiedName(imp.getTypeName().toString(), genericParametersCount);
						if (type.isPresent()){
							return type;
						}
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
	private Optional<TypeVariable> typeForQualifiedName(String name, int genericParametersCount) {

		TypeSearchParameters filter = new TypeSearchParameters(name,  genericParametersCount);
		
		return resolver.resolveType(filter).map(t  -> (TypeVariable)t);

	}

	/**
	 * @param i 
	 * @param name
	 * @param myType
	 */
	public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
		return resolver.registerType(type, genericParametersCount);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public Optional<TypeDefinition> resolvePackageForName(String name) {
		return Optional.empty();
	}

	public Map<Integer ,TypeDefinition> typeAllForName(String name) {
		return resolver.resolveTypesMap(name);
		
	}





}
