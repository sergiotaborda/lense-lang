/**
 * 
 */
package lense.compiler.context;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import compiler.lexer.ScanPosition;
import lense.compiler.CompilationError;
import lense.compiler.TypeNotFoundError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ImportDeclaration;
import lense.compiler.ast.TypeNode;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.UnionType;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class SemanticContext {

	Deque<SemanticScope> scopes = new LinkedList<SemanticScope>();
	
	private UpdatableTypeRepository resolver;
	private String currentpackage;
	private ClassTypeNode parentClassTypeNode;
	private HashMap<String, ImportDeclaration> importMappings;
	
	public SemanticContext(UpdatableTypeRepository resolver, String currentpackage, ClassTypeNode ct, HashMap<String, ImportDeclaration> importMappings){
		this.resolver = resolver;
		this.currentpackage= currentpackage;
		this.parentClassTypeNode = ct;
		this.importMappings = importMappings;
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
			
			if (name.contains(".")) {
				if (this.importMappings.containsValue(name)) {
					type = typeForQualifiedName(name, genericParametersCount);
					if (type.isPresent()){
						return type.get();
					} 
				}
			} else {
				var  importDeclaration = this.importMappings.get(name);
				
				if (importDeclaration != null) {
					type = typeForQualifiedName(importDeclaration.getName().getName(), genericParametersCount);
					if (type.isPresent()){
						return type.get();
					} 
				}
			}
			
			
			
			for (lense.compiler.Import imp : parentClassTypeNode.imports()){
				if (imp.getMatchAlias().equals(name)){
					type = typeForQualifiedName(imp.getTypeName().toString(), genericParametersCount);
					if (type.isPresent()){
						return type.get();
					} else {
						throw new CompilationError(scanPosition, name + "'" + genericParametersCount + " was imported but not found");
					}
				}
				
			}
			
			throw new TypeNotFoundError(scanPosition, name + "'" + genericParametersCount);
		}
//		else if (type.get().getGenericParameters().size() != genericParametersCount) {
//		    throw new TypeNotFoundError(scanPosition, name + "'" + genericParametersCount );
//		}
		
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


			var  importDeclaration = this.importMappings.get(name);
			
			if (importDeclaration != null) {
				var type = typeForQualifiedName(importDeclaration.getName().getName(), genericParametersCount);
				if (type.isPresent()){
					return type;
				} 
			}
			
			
			// try type variable
			final SemanticScope currentScope = currentScope();
			if (currentScope != null){
				VariableInfo variableInfo = currentScope.searchVariable(name);

				if (variableInfo != null && variableInfo.isTypeVariable()){
					return Optional.of(variableInfo.getTypeVariable());
				}


			
		
				Optional<TypeVariable> type = typeForQualifiedName(this.currentpackage + "." + name, genericParametersCount);
				if (type.isPresent()){
					return type;
				}
				
				// not type variable, attach imports and look again

				for (lense.compiler.Import imp : parentClassTypeNode.imports()){
					if (imp.getMatchAlias().equals(name)){
						type = typeForQualifiedName(imp.getTypeName().toString(), genericParametersCount);
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
		
		return resolver.resolveType(filter).filter(it -> it != null).map(t  -> (TypeVariable)t);

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

	
	public SemanticContext withRepository(UpdatableTypeRepository resolver) {
		this.resolver = resolver;
		return this;
	}



	public <T extends TypeDefinition> T ensureNotFundamental(T type) {
		if (type instanceof LenseTypeDefinition && !(type instanceof UnionType)) {
			var rawType =  resolveTypeForName(type.getName(), type.getGenericParameters().size()).map( it -> (LenseTypeDefinition)it)
						//.or(() -> LenseTypeSystem.getInstance().getForName(type.getName(), type.getGenericParameters().size()))
					.orElseThrow(() -> new TypeNotFoundError((ScanPosition)null, type.getName()))
					.getTypeDefinition();
			
			if(!((LenseTypeDefinition) type).getGenericParameters().isEmpty()) {
				var spec = ((LenseTypeDefinition) type);
				
				var respec = LenseTypeSystem.getInstance().specify(rawType, spec.getGenericParameters());
				
				return (T)respec;
				
			} else {
				
				
				return  (T)rawType;
			}
		
		}
		return type;

	}

	public TypeVariable ensureNotFundamental(TypeVariable type) {

		if (type == null) {
			return null;
		}
		
		TypeVariable ensured = type;
		if (type instanceof TypeDefinition) {
			ensured = ensureNotFundamental((TypeDefinition)type);
		}

		ensured.ensureNotFundamental(t -> 
		resolveTypeForName(t.getName(),t.getGenericParameters() == null ? 0 : t.getGenericParameters().size()).get().getTypeDefinition()
		);

		return ensured;

	}

}
