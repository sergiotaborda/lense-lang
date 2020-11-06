/**
 * 
 */
package lense.compiler.phases;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.FundamentalTypesModuleContents;
import lense.compiler.Import;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ImportDeclarationsListNode;
import lense.compiler.ast.SingleImportNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.context.SemanticContext;
import lense.compiler.modules.EditableModuleDescriptor;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.PackageResolver;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class NameResolutionPhase implements CompilerPhase {

	private PackageResolver packageResolver;
	private CompilerListener listener;

	/**
	 * Constructor.
	 * @param typeRepo
	 */
	public NameResolutionPhase(PackageResolver packageResolver, CompilerListener listener) {
		this.packageResolver = packageResolver;
		this.listener= listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompilationResult apply(CompilationResult result) {
		if (result.isError()){
			return result;
		}
		try {
			UnitTypes t = (UnitTypes) result.getCompiledUnit().getAstRootNode();

			ImportDeclarationsListNode imports = t.getImports().orElse(new ImportDeclarationsListNode());
			
			for(AstNode n : imports.getChildren()){
				SingleImportNode i = (SingleImportNode)n;

				if (!i.getName().isComposed()){
					listener.error(new CompilerMessage(i.getName() + " does not exist in the default module"));
					return new CompilationResult(new CompilationError(i.getName() + " does not exist in the default module"));
				}
				
			}
			
			UpdatableTypeRepository naming = new NamingTypeRepository();
			
			for (ClassTypeNode ct :  t.getTypes()){
				// cannot share semantic context among classes

					
				if(ct.getPackageName() == null) {
					String packageName = packageResolver.resolveUnitPackageName(ct.getScanPosition().getCompilationUnit());
					
					
					ct.setPackageName(packageName);
				}
				
				SemanticContext ctx = new SemanticContext(naming, ct.getPackageName(), ct); 
				
				ct.setSemanticContext(ctx);
				
				boolean anyImported = false;
				if (imports != null){
					for(AstNode n : imports.getChildren()){
						SingleImportNode i = (SingleImportNode)n;

						if (i.getName().toString().equals("lense.core.lang.Any")) {
							anyImported = true;
						}
						ct.addImport(Import.singleType(i.getName(), i.getAlias()));
						
					}
				}
		
				TreeTransverser.transverse(ct,new NameResolutionVisitor(ct));

				for(Iterator<Import> it = ct.imports().iterator(); it.hasNext();){
					Import imp = it.next();
					if (imp.isContainer() || !imp.isUsed()) {
						listener.warn(new CompilerMessage(imp.getTypeName().getName() + " import is declared but not used in " + ct.getFullname()));
						it.remove();
					}
				}
				
//			    if (!ct.getKind().isInterface() && ct.getGenericParametersCount() > 0) {
//			    	 ct.addImport(Import.singleType(new QualifiedNameNode("lense.core.lang.reflection.ReifiedArguments"),  null).setMemberCalled(true));
//                }
//			    
//			    if (!anyImported && !ct.getName().equals("lense.core.lang.Any")) {
//			    	 ct.addImport(Import.singleType(new QualifiedNameNode("lense.core.lang.Any"),  null).setMemberCalled(true));
//			    }
			}
			
		} catch (CompilationError e){
			listener.error(new CompilerMessage(e.getMessage()));
			return new CompilationResult(e);
		}
		
	
		return result;
	}
	
	private static class NamingTypeRepository implements UpdatableTypeRepository{

		private FundamentalTypesModuleContents fundamental = new FundamentalTypesModuleContents(new EditableModuleDescriptor("lense.core", null));
		private final Map<String,Map< Integer, TypeDefinition>> types = new HashMap<>();

		@Override
		public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
			Map<Integer, TypeDefinition> map = types.get(filter.getName());
			
			if (map == null) {
				
				return fundamental.resolveType(filter);
				
			} else {
				return filter.getGenericParametersCount().map(count -> map.get(count));
			}
		}

		@Override
		public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
			Map<Integer, TypeDefinition> map = types.get(type.getName());
			
			if (map == null) {
				map = new HashMap<>();
				types.put(type.getName(), map);
			}
			
			map.put(genericParametersCount, type);
			
			return type;
		}

		@Override
		public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
			return types.get(name);
		}
		
	}
}
