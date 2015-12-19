/**
 * 
 */
package lense.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import compiler.parser.AbstractLookupTable;
import compiler.parser.EOFTerminal;
import compiler.parser.Identifier;
import compiler.parser.LookupTableRow;
import compiler.parser.Numeric;
import compiler.parser.Production;
import compiler.parser.ProductionItem;
import compiler.parser.Terminal;
import compiler.parser.Text;
import lense.compiler.LenseGrammar;

/**
 * 
 */
public class LenseLookupTable extends AbstractLookupTable {


	public LenseLookupTable(LenseGrammar g) {
		super(g,1000);
	
		this.loadTable(g, this.getClass().getResourceAsStream("table.txt"));
	}
	/**
	 * Constructor.
	 * @param rowsCount
	 */
	public LenseLookupTable(LenseGrammar g, InputStream stream ) {
		super(g,1000);
		
		loadTable(g,stream);
		
	}
	private void loadTable(LenseGrammar g, 	InputStream stream ) {
		
		if (stream != null){
			long mark = System.currentTimeMillis();
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
				
				String[] columns = reader.readLine().split("\t");
				
				Production[] productions = new Production[columns.length];
				for(int i =1; i < columns.length; i++){
					if (columns[i].equals("EOF")){
						productions[i] = EOFTerminal.instance();
					} else if (columns[i].equals("ID")){
						productions[i] = Identifier.instance();
					} else if (columns[i].equals("Numeric")){
						productions[i] = Numeric.instance();
					} else if (columns[i].equals("Text")){
						productions[i] = Text.instance();
					} else if (columns[i].equals("Version")){
						productions[i] = VersionLiteral.instance();
					} else if (columns[i].startsWith("'")){
						productions[i] = Terminal.of(columns[i].substring(1, columns[i].length() - 1));
					} else {
						productions[i] = g.getNonTerminal(columns[i]);
						if (!g.hasNonTerminal(columns[i])){
							throw new RuntimeException("Cannot load " + columns[i]);
						}
					}
					
					this.columns.add(productions[i]);
				}
	
				
				String line;
				while (( line = reader.readLine()) != null){
					
					String[] row = line.split("\t");

					LookupTableRow r = new LookupTableRow(this, Integer.parseInt(row[0]));
					for(int i =1; i < row.length; i++){
						AddAction(productions[i],row[i], r, i);	
					}
					this.rows.add(r);
				}
				
			} catch (IOException e) {
				// no-op
			}
			long time = System.currentTimeMillis() - mark;
			
			System.out.println("Read Table from file " + time + "ms");
		}
	}

	private void AddAction(Production production, String action, LookupTableRow r, int i) {
		
		if (action.startsWith("T")){
			int pos = action.indexOf('[');
			int end = action.indexOf(']', pos);
			String[] list = action.substring(pos+1, end).split(",");
			
			for(String act : list){
				AddAction(production, act.trim(), r, i);
			}
			
		} else if (action.startsWith("S")){
			r.addShift(production, Integer.parseInt(action.substring(1)));
		}else if (action.startsWith("E")){
			// no-op
		}else if (action.startsWith("G")){
			r.addGoto(production, Integer.parseInt(action.substring(1)));
		}else if (action.startsWith("R")){
			r.addReduce(production, Integer.parseInt(action.substring(1)));
		}else if (action.startsWith("A")){
			r.addAccept(production, null);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductionItem getFinalProductionItem(int targetId) {
		return getGrammar().getFinalProductionItem(targetId);
	}

}
