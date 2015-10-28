/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.Kind;
import lense.compiler.ast.AnnotadedSenseAstNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ImplementedInterfacesNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypeParametersListNode;


/**
 * 
 */
public class ClassTypeNode extends AnnotadedSenseAstNode {

	private Kind kind;
	private String name;
	private ClassBodyNode body;
	private TypeNode superType;
	private TypeParametersListNode parametricTypesNode;
	private ImplementedInterfacesNode interfaces;
	
	
	public ClassTypeNode (Kind kind){
		this.kind = kind;
	}
	
	public Kind getKind(){
		return kind;
	}
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	public ClassBodyNode getBody() {
		return body;
	}

	public void setBody(ClassBodyNode body) {
		this.body = body;
		this.add(body);
	}

	public void setSuperType(TypeNode upperType) {
		this.superType = upperType;
		this.add(upperType);
	}

	/**
	 * Obtains {@link TypeNode}.
	 * @return the superType
	 */
	public TypeNode getSuperType() {
		return superType;
	}


	/**
	 * @param parametricTypesNode
	 */
	public void setGenerics(TypeParametersListNode parametricTypesNode) {
		this.parametricTypesNode = parametricTypesNode;
		this.add(parametricTypesNode);
	}


	public TypeParametersListNode getGenerics() {
		return parametricTypesNode;
	}

	/**
	 * @param implementedInterfacesNode
	 */
	public void setInterfaces(ImplementedInterfacesNode implementedInterfacesNode) {
		this.interfaces = implementedInterfacesNode;
		this.add(interfaces);
	}
	
	public ImplementedInterfacesNode getInterfaces(){
		return interfaces;
	}






}
