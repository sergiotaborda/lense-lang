/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.repository.Version;

/**
 * 
 */
public class VersionNode extends LenseAstNode {

	String[] parts;
	private boolean matchExact;
	private Version version;
	
	public VersionNode (String version, boolean matchExact){
		this.matchExact = matchExact;
		
		parts = version.split("\\.");
		
		int[] iparts = new int[parts.length];
		for(int i=0; i < parts.length; i++){
			iparts[i] = Integer.parseInt(parts[i]);
		}
		this.version = new Version(iparts);
	}
	
	public boolean mustMatchExactly(){
		return matchExact;
	}
	public String[] getParts(){
		return parts;
	}
	
	public Version getVersion(){
		return version;
	}
}
