package lense.compiler;


import lense.compiler.tools.ArgumentParser;
import lense.compiler.tools.LenseCompilerTool;
import lense.compiler.tools.LenseRunTool;
import lense.compiler.tools.ToolException;

public class Lense {

	public static void main(String[] args) {
	    try{
	        new Lense().run(new ArgumentParser().parse(args));
	    } catch (ToolException e){
	        println(e.getMessage());
	        help();
	    }
	}
	
	public void run(Arguments arguments) {
	    switch (arguments.getCommand()){
        case  COMPILE:
            new LenseCompilerTool().run(arguments);
            break;
        case RUN:
            new LenseRunTool().run(arguments);
            break;
        default:
            help();
        }
	}

	private static void help() {
		println("Usage: lense compile|run|help [--source=path] [--repo=path] ");
	}

	private static void println(String text) {
		System.out.println(text);
	}

}
