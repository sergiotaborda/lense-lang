/**
 * 
 */
package lense.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import compiler.parser.ItemState;
import compiler.parser.ItemStatesLookupTable;
import compiler.parser.LALRAutomatonFactory;
import compiler.parser.LookupTable;
import compiler.parser.LookupTableAction;
import compiler.parser.LookupTableRow;
import compiler.parser.Production;

/**
 * 
 */
public class BuildFromTable {

	public static void main(String[] args) throws IOException {
		LenseGrammar g = new LenseGrammar();
		System.out.println("Creating table ...");
		long mark = System.currentTimeMillis();
		ItemStatesLookupTable table = new LALRAutomatonFactory().create().produceLookupTable(g);
		long time =  System.currentTimeMillis() - mark;
		
		assertNotNull(table);
		
		System.out.println("Table Created (in " + time + " ms)");
		System.out.println("Writing Table ... ");
		
		try(FileWriter writer = new FileWriter(new File("./states.txt"))){
			
			writer.write(table.getStates().toString());
			writer.flush();
		}
	
		final File out = new File("./table.txt");
		writeTableToFile(table, out);
		
		LenseLookupTable stable = new LenseLookupTable(g, new FileInputStream(out));
		
		
		writeTableToFile(stable,  new File("./tableRef.txt"));
		assertEquals(table, stable);
		
		final File finalOut = new File("./src/main/java/lense/compiler/table.txt");
		final File finalOutPrevious = new File("./src/main/java/lense/compiler/table_previous.txt");
		Files.delete(finalOutPrevious.toPath());
		Files.copy(finalOut.toPath(), finalOutPrevious.toPath());
		
		Files.delete(finalOut.toPath());
		Files.copy(out.toPath(), finalOut.toPath());
		
		System.out.println("Writing Done");
		System.out.println(table.getStates().size() + " states");
		System.out.println("Analysing conflits...");
		int shiftReduce = 0;
		int reduceReduce = 0;
		for(ItemState state : table.getStates()){
			if (state.hasShiftReduceConflit()){
				shiftReduce++;
			} else if (state.hasReduceReduceConflit()){
				reduceReduce++;
			}
		}
		
		
		System.out.println("Shift/Reduce Conflits: " + shiftReduce);
		System.out.println("Reduce/Reduce Conflits: " + reduceReduce);
	}
	
	private static void writeTableToFile(LookupTable table,File out ) throws IOException {
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
