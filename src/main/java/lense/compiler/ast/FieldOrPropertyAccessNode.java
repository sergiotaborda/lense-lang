/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;



/**
 * 
 */
public class FieldOrPropertyAccessNode extends NeedTypeCalculationNode {

	public enum FieldKind {
		PROPERTY,
		FIELD
	}
	
	private AstNode primary;
	private String name;
	private FieldKind kind;


	/**
	 * Constructor.
	 * @param string
	 */
	public FieldOrPropertyAccessNode(String name) {
		setName(name);
		this.kind = FieldKind.FIELD;
	}
	
	public String toString(){
	    return (primary != null ? primary.toString() : "") + "." + name;
	}

	/**
	 * @param string
	 */
	public void setName(String name) {
		if (name == null){
			throw new IllegalArgumentException();
		}
		this.name= name;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setType(TypeVariable type){
		super.setTypeVariable(type);
	}
	
	public AstNode getPrimary() {
		return primary;
	}
	public void setPrimary(AstNode primary) {
		this.primary = primary;
		this.add(primary);
	}
	
	public void replace(AstNode node, AstNode newnode){
		super.replace(node, newnode);
		
		if (node.equals(primary)){
			primary = newnode;
		}
	}

	public void setKind(FieldKind kind) {
		this.kind = kind;
	}
	
	public FieldKind getKind() {
		return this.kind;
	}

}
