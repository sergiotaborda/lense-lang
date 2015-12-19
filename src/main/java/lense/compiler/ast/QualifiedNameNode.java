/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class QualifiedNameNode extends LenseAstNode {

	private StringBuilder name = new StringBuilder();

	
	public QualifiedNameNode (){}
	public QualifiedNameNode (String name){
		if (name.length() == 0){
			throw new IllegalArgumentException("Name is mandatory");
		}
		this.name= new StringBuilder(name);
	}
	
	public boolean equals(Object other){
		return other instanceof QualifiedNameNode && ((QualifiedNameNode)other).name.toString().equals(name.toString());
	}
	
	public int hashCode(){
		return name.toString().hashCode();
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
	
	public QualifiedNameNode getNext() {
		int pos = name.indexOf(".");
		if (pos < 0){
			return null;
		} else {
			return new QualifiedNameNode(name.subSequence(pos + 1, name.length() ).toString());
		}
	}
	
	public QualifiedNameNode getLast() {
		int pos = name.lastIndexOf(".");
		if (pos < 0){
			return this;
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
	/**
	 * 
	 */
	public QualifiedNameNode getFirst() {
		int pos = name.indexOf(".");
		if (pos < 0){
			return this;
		} else {
			return new QualifiedNameNode(name.substring(0, pos));
		}
	}
	/**
	 * @param qnt
	 * @return
	 */
	public QualifiedNameNode concat(QualifiedNameNode qnt) {
		return new QualifiedNameNode(this.getName() + "." + qnt.getName());
	}
}
