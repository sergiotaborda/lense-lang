/**
 * 
 */
package lense.compiler.ast;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;

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

    public MethodInvocationNode (){}

    //	public MethodInvocationNode (AstNode access ,String name, AstNode ... arguments){
    //		setCall(new MethodCallNode(name, new ArgumentListNode(arguments)));
    //		setAccess(access);
    //	}

    public MethodInvocationNode (AstNode access ,String name, ArgumentListItemNode singleArgument){
        setCall(new MethodCallNode(name, new ArgumentListNode(singleArgument)));
        setAccess(access);
    }

    public MethodInvocationNode (AstNode access ,String name, ArgumentListNode ... arguments){
        setCall(new MethodCallNode(name, new ArgumentListNode(arguments)));
        setAccess(access);
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
}
