/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class ModuleImportNode extends LenseAstNode {

	/**
	 * Constructor.
	 * @param qualifiedNameNode
	 * @param versionNode
	 */
	public ModuleImportNode(QualifiedNameNode qualifiedNameNode, VersionNode versionNode) {
		add(qualifiedNameNode);
		add(versionNode);
	}

	public QualifiedNameNode getQualifiedNameNode(){
		return (QualifiedNameNode) this.getChildren().get(0);
	}
	
	public VersionNode getVersionNode(){
		return (VersionNode) this.getChildren().get(1);
	}
}
