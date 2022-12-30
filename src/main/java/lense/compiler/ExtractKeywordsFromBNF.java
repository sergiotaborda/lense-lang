/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import compiler.AstCompiler;
import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.ListCompilationUnitSet;
import compiler.SourceFileCompilationUnit;
import compiler.bnf.BnfCompiler;
import compiler.bnf.Literal;
import compiler.bnf.Rule;
import compiler.bnf.RuleRef;
import compiler.bnf.RulesList;
import compiler.bnf.ToJavaBackEnd;
import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourcePath;
import compiler.syntax.AstNode;

/**
 * 
 */
public class ExtractKeywordsFromBNF {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		var rootFolder = DiskSourceFileSystem.instance().folder(new File(".").getAbsoluteFile().getParentFile());
		
		var file =  rootFolder.file(SourcePath.of("src","main","resources","lense", "lense.bnf")); 

		var javaOut =  rootFolder.file(SourcePath.of("src","main","java","lense","compiler", "AbstractLenseGrammar.java")); 

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		final AstCompiler compiler = new BnfCompiler();

		try {
			compiler.parse(unitSet).sendTo(new KeyWordsBackend());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Done");
	}

}

class KeyWordsBackend implements CompilerBackEnd {

	@Override
	public void use(CompiledUnit unit) {
	
		AstNode node = unit.getAstRootNode();
		
		Set<String> words = new HashSet<>();
		
		collect(node, words);
		
		var keywords = words.stream()
				.filter(it -> Character.isAlphabetic(it.charAt(0)))
				.sorted()
				.toList();
		
		var operators = words.stream()
				.filter(it -> !Character.isAlphabetic(it.charAt(0)))
				.filter(it -> !it.equals("}") && !it.equals("{") )
				.sorted()
				.toList();
		
		
		
		try (PrintWriter writer = new PrintWriter(System.out)){
			var cols = Math.ceil(Math.sqrt(keywords.size()));
			var rows = Math.ceil(keywords.size() / cols);
			writer.println("|====");
		
			var iterator = keywords.iterator();
			for (int r = 0; r < rows; r++ ) {
				writer.print("| ");
				for (int c = 0; c < cols; c++ ) {
					if (iterator.hasNext()) {
						var word = iterator.next();
						writer.print(word);
						writer.print(" | ");
					}
				}
				writer.println();
			}	
			writer.println("|====");
			
			writer.println("|====");
			for ( var word : keywords) {
				writer.print("| ");
				writer.print(word);
				writer.println(" | ");
			}	
			writer.println("|====");
			
			writer.println("|====");
			 cols = Math.ceil(Math.sqrt(operators.size()));
			 rows = Math.ceil(operators.size() / cols);
		    iterator = operators.iterator();
			for (int r = 0; r < rows; r++ ) {
				writer.print("| ");
				for (int c = 0; c < cols; c++ ) {
					if (iterator.hasNext()) {
						var word = iterator.next();
						writer.append('`').append(word).append('`');
						writer.print(" | ");
					}
				}
				writer.println();
			}
			writer.println("|====");
			
			writer.println("|====");
			for ( var word : operators) {
				writer.print("| ");
				writer.print(word);
				writer.println(" | ");
			}	
			writer.println("|====");
		}
	}
	
	private void collect(AstNode node , Set<String> words ) {
		
		if (node instanceof Literal literal) {
			words.add(literal.getName());
		}
		
		for (var item : node.getChildren()) {
			collect(item , words );
		}
	}
	
}
