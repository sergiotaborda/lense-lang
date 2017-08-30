/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class ModuleExportNode extends LenseAstNode {

	private boolean includeAllSubLevels;

    public ModuleExportNode(){}

	/**
	 * Constructor.
	 * @param qualifiedNameNode
	 */
	public ModuleExportNode(QualifiedNameNode qualifiedNameNode, boolean includeAllSubLevels) {
		add(qualifiedNameNode);
		this.includeAllSubLevels = includeAllSubLevels;
	}
	
	public boolean doesIncludeAllSubLevels(){
	    return this.includeAllSubLevels;
	}
	
	public String getName(){
	    return this.getFirstChild().toString();
	}
}
