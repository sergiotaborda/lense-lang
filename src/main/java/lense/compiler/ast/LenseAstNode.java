/**
 * 
 */
package lense.compiler.ast;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import compiler.syntax.AstNode;

/**
 * 
 */
public abstract class LenseAstNode extends AstNode{

	public <N extends AstNode> Optional<N> findChild(Class<N> childType) {
		
		Deque<AstNode> stack = new LinkedList<AstNode>(this.getChildren());
		
		while(!stack.isEmpty()){
			AstNode n = stack.pop();
			
			if (childType.isInstance(n)){
				return Optional.of(childType.cast(n));
			}
			
			for(AstNode o : n.getChildren()){
				stack.push(o);
			}
		}
		
		
		return Optional.empty();
	}
}
