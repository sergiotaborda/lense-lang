/**
 * 
 */
package lense.compiler;

import compiler.Language;
import compiler.parser.LookupTable;
import compiler.parser.Parser;

/**
 * 
 */
public class LenseLanguage extends Language{

	/**
	 * Constructor.
	 * @param grammar
	 */
	public LenseLanguage() {
		super(new LenseGrammar());
	}
	
	public LookupTable getLookupTable() {
		return new LenseLookupTable((LenseGrammar)this.getGrammar());
	}
	
	public Parser parser() {
		return new LenseParser(this);
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public AstNode transform(ParserTreeNode root,TypeResolver resolver) {
//		UnitTypes t = root.getProperty("node", UnitTypes.class).orElse(null);
//		
//		
//		if (t == null){
//			throw new RuntimeException("Compilation error");
//		}
//		LenseSemantic semantic = new LenseSemantic(resolver);
//		
//		// Garanties Semantic is correct
//		semantic.analise(t);
//		
//		// Transform literals to instances of objects
//		TreeTransverser.tranverse(t,new LiteralsInstanciatorVisitor());
//		
//		return t;
//	}

}
