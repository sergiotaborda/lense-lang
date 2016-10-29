package lense.compiler;

import java.util.function.Consumer;

import compiler.Grammar;
import compiler.lexer.ParseState;
import compiler.lexer.ScanPosition;
import compiler.lexer.Token;
import compiler.lexer.TokenState;

public class LenseHexadecimalNumberLiteralTokenState  extends TokenState {

	 
	public LenseHexadecimalNumberLiteralTokenState(TokenState other) {
		super(other.getScanner());
		this.builder = other.getBuilder();
	}

	@Override
	public ParseState receive(ScanPosition pos, char c,Consumer<Token> tokensQueue) {
		Grammar grammar = this.getScanner().getGrammar();
		
		builder.append(c);
		
		if (builder.toString().matches("^#([A-Fa-f0-9_]+)$")){
			return this;
		} else {
			builder.deleteCharAt(builder.length() - 1);
			tokensQueue.accept(grammar.terminalMatch(pos,builder.toString()).get());
			return this.getScanner().newInitialState().receive(pos, c, tokensQueue);
		}
		
	}

}
