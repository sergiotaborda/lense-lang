package lense.compiler;

import java.util.function.Consumer;

import compiler.Grammar;
import compiler.lexer.ParseState;
import compiler.lexer.ScanPosition;
import compiler.lexer.Token;
import compiler.lexer.TokenState;

public class LenseBinaryLiteralTokenState extends TokenState {

	public LenseBinaryLiteralTokenState(TokenState other) {
		super(other.getScanner());
		this.builder = other.getBuilder();
	}

	@Override
	public ParseState receive(ScanPosition pos, char c,Consumer<Token> tokensQueue) {
		Grammar grammar = this.getScanner().getGrammar();

		if (c == '1' || c == '0' || c == '_'){
			builder.append(c);
			return this;
		} else {
		
			tokensQueue.accept(grammar.terminalMatch(pos,builder.toString()).get());
			return this.getScanner().newInitialState().receive(pos, c, tokensQueue);
		}
		
	}

}
