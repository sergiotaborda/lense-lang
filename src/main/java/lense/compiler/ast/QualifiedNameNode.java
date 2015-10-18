/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.LenseAstNode;



/**
 * 
 */
public class QualifiedNameNode extends LenseAstNode {

	private StringBuilder name = new StringBuilder();

	
	public QualifiedNameNode (){}
	public QualifiedNameNode (String name){
		this.name= new StringBuilder(name);
	}
	
	public String getName() {
		return name.toString();
	}

	public void setName(String name) {
		this.name = new StringBuilder(name);
	}

	/**
	 * @param string
	 */
	public void append(String s) {
		if (name.length() > 0){
			name.append(".");
		}
		name.append(s);
	}
	
	public String toString(){
		return name.toString();
	}
	/**
	 * @return
	 */
	public QualifiedNameNode getPrevious() {
		int pos = name.lastIndexOf(".");
		if (pos < 0){
			return null;
		} else {
			return new QualifiedNameNode(name.subSequence(0, pos).toString());
		}
	}
	
	public QualifiedNameNode getLast() {
		int pos = name.lastIndexOf(".");
		if (pos < 0){
			return null;
		} else {
			return new QualifiedNameNode(name.substring(pos + 1));
		}
	}
	/**
	 * @return
	 */
	public boolean isComposed() {
		return name.indexOf(".") > 0;
	}
}
