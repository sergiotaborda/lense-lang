/**
 * 
 */
package lense.compiler.ast;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;

import compiler.syntax.AstNode;

/**
 * 
 */
public abstract class LenseAstNode extends AstNode{

	public <N extends AstNode> Optional<N> findFirstChild(Class<N> childType) {
		
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
	
	public Optional<AstNode> findFirstChild(Predicate<AstNode> predicate) {
        
        Deque<AstNode> stack = new LinkedList<AstNode>(this.getChildren());
        
        while(!stack.isEmpty()){
            AstNode n = stack.pop();
            
            if (predicate.test(n)){
                return Optional.of(n);
            }
            
            for(AstNode o : n.getChildren()){
                stack.push(o);
            }
        }
        
        
        return Optional.empty();
    }
	
	public Collection<AstNode> findChilds(Predicate<AstNode> predicate) {
        
        Deque<AstNode> stack = new LinkedList<AstNode>(this.getChildren());
        Collection<AstNode> found = new LinkedList<>();
        
        while(!stack.isEmpty()){
            AstNode n = stack.pop();
            
            if (predicate.test(n)){
                found.add(n);
            }
            
            for(AstNode o : n.getChildren()){
                stack.push(o);
            }
        }
        
        
        return found;
    }
}
