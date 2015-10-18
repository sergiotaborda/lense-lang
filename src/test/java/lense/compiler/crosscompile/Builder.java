/**
 * 
 */
package lense.compiler.crosscompile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import lense.compiler.LenseGrammar;
import lense.compiler.LenseLookupTable;

import org.junit.Test;

import compiler.Compiler;
import compiler.FileCompilationUnit;
import compiler.bnf.BnfCompiler;
import compiler.bnf.ToJavaBackEnd;
import compiler.lexer.ListCompilationUnitSet;
import compiler.parser.ItemStatesLookupTable;
import compiler.parser.LALRAutomatonFactory;
import compiler.parser.LookupTable;
import compiler.parser.LookupTableAction;
import compiler.parser.LookupTableRow;
import compiler.parser.Production;

/**
 * 
 */
public class Builder {

	@Test
	public void test() throws IOException {
		
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/test/resources/sense.bnf");
		File javaOut = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/java/compiler/sense/AbstractSenseGrammar.java");

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));


		final Compiler compiler = new BnfCompiler();
		compiler.addBackEnd(new ToJavaBackEnd(javaOut, "compiler.sense.AbstractSenseGrammar"));
		compiler.compile(unitSet);
	}

	@Test 
	public void testProduceTableAndTestLALR() throws IOException  {

		LenseGrammar g = new LenseGrammar();
		
		ItemStatesLookupTable table = new LALRAutomatonFactory().create().produceLookupTable(g);
		
		assertNotNull(table);
		
		try(FileWriter writer = new FileWriter(new File("./states.txt"))){
			
			writer.write(table.getStates().toString());
			writer.flush();
		}
		
		final File out = new File("./table.txt");
		writeTableToFile(table, out);
		
		LenseLookupTable stable = new LenseLookupTable(g, new FileInputStream(out));
		
		
		writeTableToFile(stable,  new File("./tableRef.txt"));
		assertEquals(table, stable);
		
	}
	
	private void writeTableToFile(LookupTable table,File out ) throws IOException {
		try(FileWriter writer = new FileWriter(out)){
			
			writer.append('\t');
			for( Production p  : table.columns()){
				
				writer.append(p.toString()).append('\t');
			}
			
			writer.append('\n');
			for( LookupTableRow row : table){
				
				writer.append(Integer.toString(row.getId()));
				writer.append('\t');
				
				for( Production p  : table.columns()){
					LookupTableAction action = row.getActionFor(p);
					if (action != null){
						writer.append(action.toString());
					}
					writer.append('\t');
				}

				writer.append('\n');
			}
		}
	}
}
