package lense.compiler;

public class ArgumentParser {

	
	public Arguments parse(String[] args){
		
		Arguments arguments = new Arguments();
		if (args.length != 0) {
			
			if (args[0].equalsIgnoreCase("compile")){
				arguments.setCommand(LenseCommand.COMPILE);
			} else if (args[0].equalsIgnoreCase("run")){
				arguments.setCommand(LenseCommand.RUN);
			}  else if (args[0].equalsIgnoreCase("doc")){
				arguments.setCommand(LenseCommand.DOC);
			}
		}
		
		return arguments;
				
	}
}
