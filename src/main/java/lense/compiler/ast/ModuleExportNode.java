/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class ModuleExportNode extends LenseAstNode {

	public ModuleExportNode(){}

	/**
	 * Constructor.
	 * @param qualifiedNameNode
	 */
	public ModuleExportNode(QualifiedNameNode qualifiedNameNode, boolean includeAllSubClasses) {
		add(qualifiedNameNode);
	}
}
