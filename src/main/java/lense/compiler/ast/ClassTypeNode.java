/**
 * 
 */
package lense.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import lense.compiler.Import;
import lense.compiler.Visibility;
import lense.compiler.ast.AnnotadedLenseAstNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ImplementedInterfacesNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypeParametersListNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.Kind;
import lense.compiler.type.LenseTypeDefinition;


/**
 * 
 */
public class ClassTypeNode extends AnnotadedLenseAstNode {

	private Kind kind;
	private String name;
	private ClassBodyNode body;
	private TypeNode superType;
	private TypeParametersListNode parametricTypesNode;
	private ImplementedInterfacesNode interfaces;
	
	private List<Import> imports = new ArrayList<Import>();
	private SemanticContext semanticContext;
	private LenseTypeDefinition myType;
	
	private boolean isNative;
	private boolean isAbstract;
	private Visibility visibility;
	
	public ClassTypeNode (Kind kind){
		this.kind = kind;
	}
	
	/**
	 * @param import1
	 */
	public void addImport(Import imp) {
		imports.add(imp);
	}
	
	/**
	 * @param lenseImport
	 */
	public void removeImport(Import imp) {
		imports.remove(imp);
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

	/**
	 * 
	 */
	public List<Import> imports() {
		return this.imports;
	}

	public SemanticContext getSemanticContext() {
		return semanticContext;
	}

	public void setSemanticContext(SemanticContext semanticContext) {
		this.semanticContext = semanticContext;
	}

	/**
	 * @param myType
	 */
	public void setTypeDefinition(LenseTypeDefinition myType) {
		this.myType= myType;
	}

	public LenseTypeDefinition getTypeDefinition() {
		return this.myType;
	}

	/**
	 * @return
	 */
	public int getGenericParametersCount() {
		if (getGenerics() != null){
			return getGenerics().getChildren().size();
		}else {
			return 0;
		}
		
	}

	public boolean isNative() {
		return isNative;
	}

	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public String getPackageName() {
		int pos = name.lastIndexOf('.');
		if (pos >0){
			return name.substring(0, pos);
		}
		return "";
	}
	
	public String getSimpleName() {
		int pos = name.lastIndexOf('.');
		if (pos >0){
			return name.substring(pos+1);
		}
		return name;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}









}
