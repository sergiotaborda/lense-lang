/**
 * 
 */
package lense.compiler;

import compiler.lexer.*;
import java.util.Optional;
import java.util.function.Consumer;

import compiler.Grammar;
import compiler.lexer.TokenState;

/**
 * 
 */
public class LenseNumberLiteralTokenState extends TokenState {

	/**
	 * Constructor.
	 * @param table
	 */
	public LenseNumberLiteralTokenState(TokenState other) {
		super(other.getScanner());
		this.builder = other.getBuilder();
	}

	/**
	 * @param c
	 * @return
	 */
	public ParseState receive(ScanPosition pos,char c, Consumer<Token> tokensQueue) {
		
		Grammar grammar = this.getScanner().getGrammar();
				
		int dotpos = builder.indexOf(".");
		if ( c == '.' && dotpos >= 0){
			
			if (dotpos == builder.length() - 1){
				// found .. in the stream
				
				// test together
				Optional<Token> together = grammar.maybeMatch(pos,"..");

				if (together.isPresent()){
					
					builder.deleteCharAt(builder.length() -1);
				
					tokensQueue.accept(grammar.terminalMatch(pos,builder.toString()).get());
					
					builder = new StringBuilder("..");
					
					return new OperatorTokenState(this);
				} else {
					tokensQueue.accept(grammar.terminalMatch(pos,builder.toString()).get());
					return this.getScanner().newInitialState().receive(pos, c, tokensQueue);
				}
			} else {
				// found another dot after a dot is already present, but not imediatly, ex;  1.2.
				builder.append(c);
				return ((LenseScanner)this.getScanner()).getVersionLiteralTokenState(this);
			}
			
		} else if ( grammar.isDigit(c)){
			builder.append(c);
		} else if (grammar.isAlphabetic(c)){
			if (builder.charAt(builder.length() - 1) == '.'){
				builder.deleteCharAt(builder.length() - 1);
				tokensQueue.accept(grammar.terminalMatch(pos,builder.toString()).get());
				tokensQueue.accept(grammar.terminalMatch(pos,".").get());
			} else {
				tokensQueue.accept(grammar.terminalMatch(pos,builder.toString()).get());
			}
			
			return this.getScanner().newInitialState().receive(pos, c, tokensQueue);
		} else if (grammar.isStopCharacter(c) ){
			tokensQueue.accept(grammar.terminalMatch(pos,builder.toString()).get());
			return this.getScanner().newInitialState().receive(pos, c, tokensQueue);
		 } else {
			 builder.append(c);
		 }
		 return this;
	}
}
