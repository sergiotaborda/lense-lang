/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.IndexerProperty;


/**
 * 
 */
public class IndexedPropertyReadNode extends NeedTypeCalculationNode{
	
	private ArgumentListNode arguments;
	private AstNode access;
	private IndexerProperty indexerProperty;
	
	public IndexedPropertyReadNode(){}

	/**
	 * @param astNode
	 */
	public void setAccess(AstNode node) {
		this.access = node;
		this.add(node);
	}


	/**
	 * Obtains {@link AstNode}.
	 * @return the access
	 */
	public AstNode getAccess() {
		return access;
	}
	

	public ArgumentListNode getArguments() {
		return arguments;
	}


	public void setArguments(ArgumentListNode arguments) {
		this.arguments = arguments;
		this.add(arguments);
	}

    public IndexerProperty getIndexerProperty() {
        return indexerProperty;
    }

    public void setIndexerProperty(IndexerProperty indexerProperty) {
        this.indexerProperty = indexerProperty;
    }


}
