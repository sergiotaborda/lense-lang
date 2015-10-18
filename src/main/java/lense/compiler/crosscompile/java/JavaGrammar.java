/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.util.Optional;
import java.util.Set;

import compiler.lexer.ScanPosition;
import compiler.lexer.Token;

/**
 * 
 */
public class JavaGrammar extends AbstractJavaGrammar {

	
	public JavaGrammar (){
		super();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIgnore(char c) {
		return c == '\t' || c == '\r' ||  c== ' ' || c == '\n' ;
	}
	
	protected void addStopCharacters(Set<Character> stopCharacters) {
		stopCharacters.add('"');
		stopCharacters.add('(');
		stopCharacters.add(')');
		stopCharacters.add('{');
		stopCharacters.add('}');
		stopCharacters.add('[');
		stopCharacters.add(']');
		stopCharacters.add(';');
		stopCharacters.add(',');
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStringLiteralDelimiter(char c) {
		return c == '"';
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Token> stringLiteralMath(ScanPosition pos,String text) {
		return Optional.of(new JavaToken(text, Symbols.LiteralString));
	}
}
