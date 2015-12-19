/**
 * 
 */
package lense.compiler;

import compiler.lexer.Token;
import compiler.parser.AbstractAutoNonTerminal;
import compiler.parser.AutoNonTerminal;
import compiler.parser.TextNode;

/**
 * 
 */
public class VersionLiteral extends AbstractAutoNonTerminal implements AutoNonTerminal{

	private static VersionLiteral me = new VersionLiteral();
	
	/**
	 * @param string 
	 * @return
	 */
	public static VersionLiteral instance() {
		return me;
	}
	
	/**
	 * Constructor.
	 * @param name
	 */
	protected VersionLiteral() {
		super("Version");
		this.addSemanticAction((p , r) -> {
			 p.setSemanticAttribute("lexicalValue", r.get(0).getSemanticAttribute("lexicalValue").get());
			 p.setSemanticAttribute("node", new TextNode((String)r.get(0).getSemanticAttribute("lexicalValue").get()));
		});
	}
	
	public String toString(){
		return "Version";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNonTerminal() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(Token token) {
		return token instanceof VersionLiteralToken;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isText() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIdentifier() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNumeric() {
		return false;
	}

}
