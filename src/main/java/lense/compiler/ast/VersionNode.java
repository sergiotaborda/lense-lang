/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class VersionNode extends LenseAstNode {

	String[] parts;
	private boolean matchExact;
	private String version;
	
	public VersionNode (String version, boolean matchExact){
		this.version = version;
		parts = version.split("\\.");
		this.matchExact = matchExact;
	}
	
	public boolean mustMatchExactly(){
		return matchExact;
	}
	public String[] getParts(){
		return parts;
	}
	
	public String getVersion(){
		return version;
	}
}
