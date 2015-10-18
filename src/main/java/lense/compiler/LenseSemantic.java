/**
 * 
 */
package lense.compiler;

import java.util.ArrayList;
import java.util.List;

import lense.compiler.ast.ClassType;
import lense.compiler.ast.ImportNode;
import lense.compiler.ast.ImportsNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.SemanticContext;
import lense.compiler.SemanticVisitor;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeResolver;

/**
 * 
 */
public class LenseSemantic {


	private TypeResolver resolver;

	public LenseSemantic(TypeResolver resolver){
		this.resolver = resolver;
	}
	/**
	 * @param t
	 */
	public void analise(UnitTypes t) {

		List<ClassType> classes = new ArrayList<>(2);
		ImportsNode imports = null;
		for(AstNode n : t.getChildren()){
			if (n instanceof ClassType){
				classes.add((ClassType)n);
			} else {
				imports = (ImportsNode)n;
			}
		}

		for (ClassType ct : classes){
			// cannot share semantic context among classes
			SemanticContext sc = new SemanticContext(resolver);

			if (imports != null){
				for(AstNode n : imports.getChildren()){
					ImportNode i = (ImportNode)n;

					// try for class
					TypeDefinition u = sc.typeForName(i.getName().getName());
					if (u == null){
						// add to names
						sc.addImportPackage(i.getName().getName());
					} else {
						// add type package to packages
						sc.addImportPackage(i.getName().getPrevious().getName());
					}
				}
			}
			
			TreeTransverser.tranverse(ct,new SemanticVisitor(sc));
		}
	}
}
