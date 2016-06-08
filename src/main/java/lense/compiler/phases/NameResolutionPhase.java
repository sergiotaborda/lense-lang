/**
 * 
 */
package lense.compiler.phases;

import java.util.Iterator;

import lense.compiler.CompilationError;
import lense.compiler.Import;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ImportDeclarationsListNode;
import lense.compiler.ast.SingleImportNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.context.SemanticContext;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.typesystem.PackageResolver;
import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;

/**
 * 
 */
public class NameResolutionPhase implements CompilerPhase {

	private UpdatableTypeRepository typeRepository;
	private PackageResolver packageResolver;
	private CompilerListener listener;

	/**
	 * Constructor.
	 * @param typeRepo
	 */
	public NameResolutionPhase(UpdatableTypeRepository typeRepository, PackageResolver packageResolver, CompilerListener listener) {
		this.typeRepository = typeRepository;
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
			
			for (ClassTypeNode ct :  t.getTypes()){
				// cannot share semantic context among classes

				String packageName = packageResolver.resolveUnitPackageName(ct.getScanPosition().getCompilationUnit());
						
				ct.setName(packageName + '.' + ct.getName());
				
				SemanticContext ctx = new SemanticContext(typeRepository, packageName); 
				
				ct.setSemanticContext(ctx);
				
				if (imports != null){
					for(AstNode n : imports.getChildren()){
						SingleImportNode i = (SingleImportNode)n;

						ct.addImport(Import.singleType(i.getName(), i.getAlias()));
						
					}
				}
		
				TreeTransverser.transverse(ct,new NameResolutionVisitor(ct));

				for(Iterator<Import> it = ct.imports().iterator(); it.hasNext();){
					Import imp = it.next();
					if (imp.isContainer() || !imp.isUsed()) {
						listener.warn(new CompilerMessage(imp.getTypeName().getName() + " import is not used."));
						it.remove();
					}
				}
				
			}
			
		} catch (CompilationError e){
			listener.error(new CompilerMessage(e.getMessage()));
			return new CompilationResult(e);
		}
		
	
		return result;
	}
}
