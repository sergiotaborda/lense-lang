/**
 * 
 */
package lense.compiler.ast;

import java.util.Map;

import compiler.syntax.AstNode;
import lense.compiler.type.Method;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class MethodInvocationNode extends NeedTypeCalculationNode {

    private MethodCallNode call;
    private AstNode access;

    private boolean propertyDerivedMethod;
    private boolean indexDerivedMethod;
    private String propertyName;
    private PropertyOperation propertyOperation;
	private Map<String, TypeVariable> boundedTypes;
	private TypeMember member;
	private boolean staticInvocation;
	private boolean enchamentCall;
	private boolean tupleAccessMethod;

    public MethodInvocationNode (){}

    public MethodInvocationNode (AstNode access , String name, ArgumentListItemNode singleArgument){
        setCall(new MethodCallNode(name, new ArgumentListNode(singleArgument)));
        setAccess(access);
    }
    
    public MethodInvocationNode (AstNode access , String name, ArgumentListItemNode ... arguments){
        setCall(new MethodCallNode(name, new ArgumentListNode(arguments)));
        setAccess(access);
    }
    
    public MethodInvocationNode (AstNode access , String name, ArgumentListNode arguments){
        setCall(new MethodCallNode(name, arguments));
        setAccess(access);
    }

    public MethodInvocationNode (Method member,AstNode access , ArgumentListItemNode singleArgument){
        setCall(new MethodCallNode(member.getName(), new ArgumentListNode(singleArgument)));
        setAccess(access);
        this.member = member;
    }
    
    public MethodInvocationNode (Method member, AstNode access , ArgumentListItemNode ... arguments){
        setCall(new MethodCallNode(member.getName(), new ArgumentListNode(arguments)));
        setAccess(access);
        this.member = member;
    }
    

    public String toString(){
        return (access != null ? access.toString() : "") + "." + call.toString();
    }
    /**
     * @param methodCallNode
     */
    public void setCall(MethodCallNode call) {
        this.call = call;
        this.add(call);
    }

    /**
     * @param astNode
     */
    public void setAccess(AstNode access) {
        this.access = access;
        this.add(access);
    }

    /**
     * Obtains {@link MethodCallNode}.
     * @return the call
     */
    public MethodCallNode getCall() {
        return call;
    }

    /**
     * Obtains {@link AstNode}.
     * @return the access
     */
    public AstNode getAccess() {
        return access;
    }

    public void replace(AstNode node, AstNode newnode){
        super.replace(node, newnode);

        if (this.access == node){
            this.access = newnode;
        }
    }

    public boolean isPropertyDerivedMethod() {
        return propertyDerivedMethod;
    }

    public void setPropertyDerivedMethod(boolean propertyDerivedMethod) {
        this.propertyDerivedMethod = propertyDerivedMethod;
    }

    public boolean isIndexDerivedMethod() {
        return indexDerivedMethod;
    }

    public void setIndexDerivedMethod(boolean indexDerivedMethod) {
        this.indexDerivedMethod = indexDerivedMethod;
        if (indexDerivedMethod){
            this.propertyDerivedMethod = true;
        }
    }

    public void setPropertyOperation(PropertyOperation propertyOperation) {
        this.propertyOperation = propertyOperation;
    }
    public PropertyOperation getPropertyOperation() {
        return this.propertyOperation;
    }

    public void setPropertyDerivedName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyDerivedName() {
        return this.propertyName;
    }

	
    public void setBoundedTypes(Map<String, TypeVariable> boundedTypes) {
    	this.boundedTypes= boundedTypes;
	}
    
    public Map<String, TypeVariable> getBoundedTypes() {
    	return this.boundedTypes;
	}

	public TypeMember getTypeMember() {
		return member;
	}
	
	public void setTypeMember(TypeMember member) {
		this.member = member;
	}

	
	public void setStaticInvocation(boolean staticInvocation) {
		this.staticInvocation = staticInvocation;
	}
	
	public boolean isStaticInvocation() {
		return this.staticInvocation;
	}

	public boolean isTupleAccessMethod() {
		return tupleAccessMethod;
	}

	public void setTupleAccessMethod(boolean tupleAccessMethod) {
		this.tupleAccessMethod = tupleAccessMethod;
	}

	public boolean isEnchamentCall() {
		return enchamentCall;
	}

	public void setEnchamentCall(boolean enchamentCall) {
		this.enchamentCall = enchamentCall;
		if (enchamentCall) {
			this.staticInvocation = true;
		}
	
	}
}
