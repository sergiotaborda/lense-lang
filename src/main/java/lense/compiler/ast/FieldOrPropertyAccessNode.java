/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.NeedTypeCalculationNode;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import compiler.syntax.AstNode;



/**
 * 
 */
public class FieldOrPropertyAccessNode extends NeedTypeCalculationNode {

	public enum Kind {
		PROPERTY,
		FIELD
	}
	
	private AstNode primary;
	private String name;
	private Kind kind;


	/**
	 * Constructor.
	 * @param string
	 */
	public FieldOrPropertyAccessNode(String name) {
		setName(name);
		this.kind = Kind.FIELD;
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

	public void setKind(Kind kind) {
		this.kind = kind;
	}
	
	public Kind getKind() {
		return this.kind;
	}

}