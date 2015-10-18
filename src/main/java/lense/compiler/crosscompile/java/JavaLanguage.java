/**
 * 
 */
package lense.compiler.crosscompile.java;

import compiler.Language;
import compiler.parser.nodes.ParserTreeNode;
import compiler.syntax.AstNode;
import compiler.typesystem.TypeResolver;

/**
 * 
 */
public class JavaLanguage extends Language{

	/**
	 * Constructor.
	 * @param grammar
	 */
	public JavaLanguage() {
		super(new JavaGrammar());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AstNode transform(ParserTreeNode root, TypeResolver resolver) {
		return new AstNode();
	}

}
