package lense.compiler.tools;

import java.util.LinkedList;
import java.util.Queue;

import lense.compiler.Arguments;
import lense.compiler.tools.LenseCommand.Parameter;

public class ArgumentParser {

	/*
	 * Parses the input. Some patter like 
	 * 
	 * lense [command|command:mode] -flag1 -flag2 ... --parameter1 --parameter2=value2
	 */
	public Arguments parse(String ... args){
		
	    Queue<ToolToken> tokenList = new LinkedList<>();
        
	    // translate command:value and parameter=value
	   
	    for (int i =0; i < args.length; i++){
	        String arg = args[i];
	        if (arg.contains(":")){
	            if (i!=0){
	                throw new IllegalCommandArgument(arg);
	            }
	            String[] commandAndMode = arg.split(":");
	
	            if (commandAndMode.length == 1){
                    throw new IllegalCommandArgument(arg);
                }
	            
	            tokenList.add(new CommandToolToken(commandAndMode[0]));
	            tokenList.add(new CommandModeToolToken(commandAndMode[1]));
	        } else if (arg.startsWith("--")){
                String parameter = arg.substring(2);
                
                String value = null;
                if (parameter.contains("=")){
                    String[] params = parameter.split("=");
                    parameter = params[0];
                    value = params[1];
                }
                
                // TODO validate only chareacters
                tokenList.add(new ParameterToolToken(parameter));
                if (value != null){
                    tokenList.add(new ParameterValueToolToken(value));
                }
            } else if (arg.startsWith("-")){
                String flag = arg.substring(1);

                // TODO validate only chareacters
                tokenList.add(new FlagToolToken(flag));
            } else if (i == 0){
	            tokenList.add(new CommandToolToken(arg));
	        } else {
	            throw new IllegalCommandArgument(arg);
	        }
	    }
	    
	    Arguments arguments = new Arguments();
	    
	    while (!tokenList.isEmpty()){
	        ToolToken token = tokenList.poll();
	        
	        if (token instanceof CommandToolToken){
	            CommandToolToken t = (CommandToolToken)token;
	            
	            if (t.getName().equalsIgnoreCase("compile")){
	                arguments.setCommand(LenseCommand.COMPILE);
	            } else if (t.getName().equalsIgnoreCase("run")){
	                arguments.setCommand(LenseCommand.RUN);
	            }  else if (t.getName().equalsIgnoreCase("doc")){
	                arguments.setCommand(LenseCommand.DOC);
	            }
	        } else if (token instanceof CommandModeToolToken){
	            CommandModeToolToken t = (CommandModeToolToken)token;
                
	            arguments.setMode(arguments.getCommand().parseMode(t.getName()));
	        } else if (token instanceof FlagToolToken){
	            FlagToolToken t = (FlagToolToken)token;
                
                // TODO
            } else if (token instanceof ParameterToolToken){
                ParameterToolToken t = (ParameterToolToken)token;
                
                Parameter param = arguments.getCommand().parseParameter(t.getName());
                String val = "";
             
                if (tokenList.peek() instanceof ParameterValueToolToken){
                    ParameterValueToolToken value = (ParameterValueToolToken)tokenList.poll();
                    val = value.getValue();
                }
                
                arguments.setParameter(param, val);
            }
	    }

		return arguments;
				
	}
}
