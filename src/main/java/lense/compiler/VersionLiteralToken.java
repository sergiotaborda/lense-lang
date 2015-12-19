/**
 * 
 */
package lense.compiler;

import java.util.Optional;

import compiler.lexer.ScanPosition;
import compiler.lexer.Token;

/**
 * 
 */
public class VersionLiteralToken implements Token {

	private ScanPosition position;
	private String lexicalValue;

	/**
	 * Constructor.
	 * 
	 * @param pos
	 * @param text
	 */
	public VersionLiteralToken(ScanPosition pos, String text) {
		this.position = pos;
		this.lexicalValue = text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanPosition getPosition() {
		return position;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStartLineComment() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStartMultiLineComment() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isId() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEndMultiLineComment() {
		return false;
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
	public boolean isEndOfLine() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStringLiteralStart() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStringLiteral() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNumberLiteral() {
		return false;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOperator() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(String text) {
		return this.lexicalValue.equals(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getText() {
		return Optional.of(lexicalValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isKeyword() {
		return false;
	}

}
