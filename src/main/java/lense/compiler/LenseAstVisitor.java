/**
 * 
 */
package lense.compiler;

import java.util.HashMap;
import java.util.Map;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;

/**
 * 
 */
public class LenseAstVisitor implements Visitor<AstNode>{

	public interface LenseAstVisitable<N extends AstNode> {
		
		public default VisitorNext visitBeforeChildren(N node) { return VisitorNext.Children; };
		public default void visitAfterChildren(N node) { };
	}
	
	
	public LenseAstVisitor (){}
	
	private Map<String, LenseAstVisitable> visitables = new HashMap<>();
	
	protected final <N extends AstNode> void register(Class<N> type, LenseAstVisitable<N> visitable){
		visitables.put(type.getName(), visitable);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startVisit() {

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final VisitorNext visitBeforeChildren(AstNode node) {
		LenseAstVisitable vistiable = visitables.get(node.getClass().getName());
		
		if (vistiable == null){
			return VisitorNext.Children;
		} else {
			return vistiable.visitBeforeChildren(node);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visitAfterChildren(AstNode node) {
		LenseAstVisitable vistiable = visitables.get(node.getClass().getName());
		
		if (vistiable != null){
			vistiable.visitAfterChildren(node);
		}
	}
	

}
