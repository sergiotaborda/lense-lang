/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.util.Optional;

import compiler.lexer.ScanPosition;
import compiler.lexer.Token;

/**
 * 
 */
public class JavaToken implements Token {

	private String text;
	private Symbols symbol;

	/**
	 * Constructor.
	 * @param text
	 * @param ordinal
	 */
	public JavaToken(String text, Symbols symbol) {
		this.text = text;
		this.symbol = symbol;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isKeyword() {
		return symbol == Symbols.Keywork;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStringLiteral() {
		return symbol == Symbols.LiteralString;
	}
	
	public String toString(){
		return  text + "\t" +  symbol.name() ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStartLineComment() {
		return symbol == Symbols.LineMultilineComment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStartMultiLineComment() {
		return symbol == Symbols.StartMultilineComment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isId() {
		return symbol == Symbols.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEndMultiLineComment() {
		return symbol == Symbols.EndMultilineComment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEndOfFile() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStringLiteralStart() {
		return symbol == Symbols.LiteralStringSurround;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNumberLiteral() {
		return symbol == Symbols.StartNumberLiteral;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOperator() {
		return symbol == Symbols.Operator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(String text) {
		return this.text.equals(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEndOfLine() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getText() {
		return Optional.of(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanPosition getPosition() {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isWholeNumber() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDecimalNumber() {
		return false;
	}


}
