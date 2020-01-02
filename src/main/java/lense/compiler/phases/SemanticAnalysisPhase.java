/**
 * 
 */
package lense.compiler.phases;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.ast.ChildTypeNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.SingleImportNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.context.SemanticContext;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class SemanticAnalysisPhase implements CompilerPhase {


	private CompilerListener listener;
	private UpdatableTypeRepository typeRepository;

	public SemanticAnalysisPhase(UpdatableTypeRepository typeRepository, CompilerListener listener){
		this.listener = listener;
		this.typeRepository = typeRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompilationResult apply(CompilationResult result) {

		if (result.isError()){
			return result;
		}

		UnitTypes types = result.getCompiledUnit() == null ? null : (UnitTypes)result.getCompiledUnit().getAstRootNode();

		if (types == null){
			return new CompilationResult(new RuntimeException("Unexpected Error. Result as no node."));
		}

		boolean hasAlgebric = false;

		try {
			
			Map<TypeVariable, List<TypeDefinition>> enhancements = new HashMap<>();
			if (types.getImports().isPresent()) {
				
				for (SingleImportNode si : types.getImports().get().getChildren(SingleImportNode.class)){
					
					Optional<TypeDefinition> using = typeRepository.resolveType(new TypeSearchParameters(si.getName().getName()));
					
					if (using.map( t -> t.getKind() != null && t.getKind().isEnhancement()).orElse(false)) {
						
						List<TypeDefinition> list = enhancements.get(using.get().getSuperDefinition());
						
						if (list == null) {
							 list = new LinkedList<>();
							 enhancements.put(using.get().getSuperDefinition(), list);
						}
						
						list.add(using.get());
					}
				}
			}
			
			for (ClassTypeNode ct : types.getTypes()){
				// cannot share semantic context among classes
				
				if (!ct.isNative()) { // TODO remove this condition

					// attach the repository with loaded types
					SemanticContext ctx = ct.getSemanticContext().withRepository(typeRepository);

					TreeTransverser.transverse(ct,new SemanticVisitor(ctx, enhancements, listener));
					TreeTransverser.transverse(ct,new EnsureNotFundamentalTypesVisitor(ctx));

					hasAlgebric = hasAlgebric || ct.isAlgebric();

			   }  else {
				   if (ct.getKind().isValue() && ct.getSuperType() != null) {
					   throw new CompilationError(ct, "Value classes cannot inherit from other classes. They can only implement interfaces.");
				   }
			   }
			}

			if (hasAlgebric) {
				Map<String,ClassTypeNode> mapping = new HashMap<>();

				for (ClassTypeNode ct : types.getTypes()){
					mapping.put(ct.getName(), ct);
					mapping.put(ct.getSimpleName(), ct);
				}

				for (ClassTypeNode ct : types.getTypes()){
					if (ct.isAlgebric()) {
						for (AstNode n : ct.getAlgebricChildren().getChildren()) {
							ChildTypeNode ctn = (ChildTypeNode)n;
							if(!mapping.containsKey(ctn.getType().getName())) {
								throw new CompilationError(ctn, "Child type " + ctn.getType().getName() + " is not defined. Child types must be defined in the same source file.");
							}
						}
					} else if (ct.getTypeDefinition().getSuperDefinition().isAlgebric()){
						if (!ct.getTypeDefinition().getSuperDefinition().getAllCases().contains(ct.getTypeDefinition())) {
							throw new CompilationError(ct, "Type " + ct.getTypeDefinition().getName() + " cannot extend " + ct.getTypeDefinition().getSuperDefinition().getName() + " becasue it is not a declared case");
						}
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
