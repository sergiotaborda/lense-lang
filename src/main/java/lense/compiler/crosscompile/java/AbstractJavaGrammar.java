package lense.compiler.crosscompile.java;
import compiler.AbstractGrammar;
import compiler.parser.NonTerminal;

public abstract class AbstractJavaGrammar extends AbstractGrammar {

	public AbstractJavaGrammar (){
		super();
	}

	protected NonTerminal defineGrammar() {
		return null;
	}
}