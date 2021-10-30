/**
 * 
 */
package lense.compiler.ast;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import lense.compiler.Import;
import lense.compiler.context.SemanticContext;
import lense.compiler.phases.ScopeDelimiter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.typesystem.Visibility;


/**
 * 
 */
public class ClassTypeNode extends AnnotadedLenseAstNode implements ScopeDelimiter{

	private LenseUnitKind kind;
	private String packageName;
	private String simpleName;
	
	private ClassBodyNode body;
	private TypeNode superType;
	private TypeParametersListNode parametricTypesNode;
	private ImplementedInterfacesNode interfaces;
	private StatisfiedTypeClassesNode satisfiedTypeClasses;
	
	private Set<Import> imports = new LinkedHashSet<Import>();
	private SemanticContext semanticContext;
	private LenseTypeDefinition myType;
	
	private boolean isNative;
	private boolean isAbstract;
	private boolean isFinal;
	private boolean isSealed;
	private boolean isAlgebric;
	private boolean isValueClass;
	private boolean isImmutable;
		
	private Visibility visibility;
	private ChildTypesListNode childTypesListNode;
    private boolean isAsStringDefined;
    private boolean isHashValueDefined;
    private boolean isEqualsToDefined;
	private GivenGenericConstraintList givens;

	
	
	public ClassTypeNode (LenseUnitKind kind){
		this.kind = kind;
	}
	
	/**
	 * @param import1
	 */
	public void addImport(Import imp) {
		if (!imp.getTypeName().toString().equals(this.getFullname())){
			imports.add(imp);
		}		
	}
	
	/**
	 * @param lenseImport
	 */
	public void removeImport(Import imp) {
		imports.remove(imp);
	}

	
	public LenseUnitKind getKind(){
		return kind;
	}
	
	public String getFullname() {
		if (this.packageName == null) {
			return simpleName;
		}
		
		return packageName + "." + simpleName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getSimpleName() {
		return simpleName;
	}

	
	public void setSimpleName(String name) {
		this.simpleName = name;
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
	
	public void setAlgebricChildren(ChildTypesListNode childTypesListNode) {
		this.childTypesListNode = childTypesListNode;
		this.add(childTypesListNode);
	}


	public ChildTypesListNode getAlgebricChildren() {
		return this.childTypesListNode;
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
	public Collection<Import> imports() {
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

	public boolean isImmutable() {
		return isImmutable;
	}

	public void setImmutable(boolean isImmutable) {
		this.isImmutable = isImmutable;
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

	@Override
	public String getScopeName() {
		return "#top";
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isSealed() {
		return isSealed;
	}

	public void setSealed(boolean isSealed) {
		this.isSealed = isSealed;
	}

	public boolean isAlgebric() {
		return isAlgebric;
	}

	public void setAlgebric(boolean isAlgebric) {
		this.isAlgebric = isAlgebric;
	}

    public void isAsStringDefined(boolean value) {
       this.isAsStringDefined = value;
    }
	public boolean isValueClass() {
		return isValueClass || this.kind.isValue();
	}

    public void isHashValueDefined(boolean value) {
        this.isHashValueDefined = value;
    }

    public void setEqualsToDefined(boolean value) {
        this.isEqualsToDefined = value;
    }

    public boolean isAsStringDefined() {
        return isAsStringDefined;
    }

    public void setAsStringDefined(boolean isAsStringDefined) {
        this.isAsStringDefined = isAsStringDefined;
    }

    public boolean isHashValueDefined() {
        return isHashValueDefined;
    }

    public void setHashValueDefined(boolean isHashValueDefined) {
        this.isHashValueDefined = isHashValueDefined;
    }

    public boolean isEqualsToDefined() {
        return isEqualsToDefined;
    }

	
    public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setGivens(GivenGenericConstraintList givens) {
		this.givens = givens;
		this.add(givens);
	}
	
	public GivenGenericConstraintList getGivens(){
		return givens;
	}

	public StatisfiedTypeClassesNode getSatisfiedTypeClasses() {
		return satisfiedTypeClasses;
	}

	public void setSatisfiedTypeClasses(StatisfiedTypeClassesNode satisfiedTypeClasses) {
		this.satisfiedTypeClasses = satisfiedTypeClasses;
		this.add(satisfiedTypeClasses);
	}








}
