package lense.compiler;


import lense.compiler.tools.ArgumentParser;
import lense.compiler.tools.LenseCompilerTool;

public class Lense {

	public static void main(String[] args) {

		new Lense().run(new ArgumentParser().parse(args));
	}
	
	public void run(Arguments arguments) {
	    switch (arguments.getCommand()){
        case  COMPILE:
            new LenseCompilerTool().run(arguments);
            break;
        case RUN:
        default:
            help();
        }
	}

	private static void help() {
		println("lense compile|run ");
	}

	private static void println(String text) {
		System.out.println(text);
	}

}
