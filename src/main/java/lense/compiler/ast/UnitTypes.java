/**
 * 
 */
package lense.compiler.ast;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



/**
 * 
 */
public class UnitTypes extends LenseAstNode {

	public List<ClassTypeNode> getTypes(){
		return this.getChildren().stream().filter( n -> n instanceof ClassTypeNode).map(n -> (ClassTypeNode)n).collect(Collectors.toList());
	}
	
	public Optional<ImportDeclarationsListNode> getImports(){
		return this.getChildren().stream().filter( n -> n instanceof ImportDeclarationsListNode).map(n -> (ImportDeclarationsListNode)n).findAny();
	}
	
	public Optional<ModuleNode> getModule(){
		return this.getChildren().stream().filter( n -> n instanceof ModuleNode).map(n -> (ModuleNode)n).findAny();
	}
}
