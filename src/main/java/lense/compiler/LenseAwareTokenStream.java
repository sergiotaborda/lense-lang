/**
 * 
 */
package lense.compiler;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import compiler.SymbolBasedToken;
import compiler.TokenSymbol;
import compiler.lexer.Token;
import compiler.lexer.TokenStream;

/**
 * 
 */
class LenseAwareTokenStream implements TokenStream {

	private TokenStream original;
	Deque<Character> openClose = new LinkedList<>();
	Queue<Token> buffer = new LinkedList<>();
	/**
	 * Constructor.
	 * @param tokens
	 */
	public LenseAwareTokenStream(TokenStream original) {
		this.original = original;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return original.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Token next() {
		Token t;
		
		if (buffer.isEmpty()){
			t = original.next();
		} else {
			t = buffer.poll();
		}

		if (t.isKeyword()){
			
			if (t.getText().get().equals("out")|| t.getText().get().equals("in")) {
				Token previous = original.peekPrevious();
				if (previous.isOperator() && previous.getText().get().equals(".")){
					// if keywords are used after a . they are not consider keywords. 
					// This is to maintain compatibility with System.in and System.out
					// TODO revise if this is really needed
					return new SymbolBasedToken(t.getPosition(), t.getText().get(),TokenSymbol.ID );
				} 
			} else if (t.getText().get().equals("value")) {
				Token previous = original.peekNext();
				if (!previous.isKeyword() || !previous.getText().get().equals("class")){
					// value is only a keyword when used before the class keyword
					return new SymbolBasedToken(t.getPosition(), t.getText().get(),TokenSymbol.ID );
				}
			}
			
		} else if (t.isOperator()){
			if (t.getText().get().equals("<")){
				openClose.push('<');
			} else if (t.getText().get().startsWith(">")){
				if(!openClose.isEmpty() && openClose.peek().equals('<')){
					if (t.getText().get().length() == 1){
						openClose.pop();
						return t;
					} else if (t.getText().get().length() > 1){
						openClose.pop();
					    SymbolBasedToken next = new SymbolBasedToken(t.getPosition(), t.getText().get().substring(0,1),TokenSymbol.Operator );
					    SymbolBasedToken after = new SymbolBasedToken(t.getPosition(), t.getText().get().substring(1),TokenSymbol.Operator );
					    buffer.add(after);
						return next;
					}
				} 
			} else if (!openClose.isEmpty() && braketscloserss.contains(t.getText().get())){
				openClose.pop();
				return t;
			}
		}
		return t;

	}
	
	private Set<String> braketscloserss = new HashSet<>(Arrays.asList("(", ")", ";"));

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TokenStream duplicate() {
		return new LenseAwareTokenStream(original.duplicate());
	}
	
	public String toString(){
		return original.toString();
	}

	public boolean equals(Object obj) {
		return (obj instanceof LenseAwareTokenStream) && equalsListTokenStream((LenseAwareTokenStream)obj); 
	}


	private boolean equalsListTokenStream(LenseAwareTokenStream other) {
		return this.original.equals(other.original);
	}
	
    public int hashCode() {
    	return this.original.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasPrevious() {
		return original.hasPrevious();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Token peekPrevious() {
		return original.peekPrevious();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Token peekNext() {
		return original.peekNext();
	}
}
